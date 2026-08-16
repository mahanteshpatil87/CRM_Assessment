# Framework Architecture

## Design pattern: Page Object Model, with locators separated from behavior

Every page is split into two classes:

- **`elements.<module>.XxxPageElements`** — every `By` locator used on that page, and nothing
  else. A protected constructor calling `super(driver)` and a list of `protected final By` fields.
- **`pages.<module>.XxxPage extends XxxPageElements`** — every action method for that page
  (`enterUsername`, `clickSave`, `loginAs`, ...), and nothing else. No locator ever appears here;
  methods reference the `By` fields inherited from the Elements class by name.

This means a locator changing because the application's markup changed touches exactly one file,
never the file containing the page's behavior — and conversely, adding a new action never requires
touching the file that defines what's on the page. Both classes ultimately extend
`pages.base.BasePage`, which holds one `ElementActions` instance and exposes thin delegation
methods (`click`, `type`, `getText`, ...), so page objects read naturally (`this.click(x)`) while
`ElementActions` remains the single place that actually touches a `WebElement`. Test classes never
contain a raw Selenium locator or call a Selenium API directly — they only call
intention-revealing page object methods (`loginPage.loginAs(user, pass)`,
`addEmployeePage.save(firstName, lastName, id)`).

Reusable widgets that appear on more than one page (custom dropdown, autocomplete, file upload,
bulk-select checkboxes) are factored out into `components/*` rather than duplicated per page
object — each page composes the components it needs rather than re-implementing the same XPath.

## Driver lifecycle

`driver.DriverFactory` is responsible only for *creating* a correctly configured `WebDriver` for a
given browser; `driver.DriverManager` owns the lifecycle (`initDriver` / `getDriver` / `quitDriver`)
via a `ThreadLocal<WebDriver>`. This costs nothing today (the suite runs single-threaded) and means
parallel execution can be turned on later (`parallel="methods"` in the TestNG suite XML) without
any structural change — no two threads can ever collide on the same browser session, since each
gets its own. `assertion.AssertsManager` follows the identical `ThreadLocal` pattern for the same
reason: a shared `SoftAssert` instance would leak one thread's failures into another's.

## Wait strategy

`utils.WaitUtils` is the single place every explicit-wait `ExpectedCondition` is defined.
`Thread.sleep()` is not used anywhere in the framework's *browser-interaction* code — every UI wait
is tied to an observable DOM condition. The one legitimate use in the whole codebase is
`utils.FileUtils.waitForDownloadedFile`'s filesystem poll (500ms between directory listing checks):
there is no WebDriver-observable signal for "the OS finished writing a browser download to disk",
so polling the filesystem is the standard, justified technique there. Earlier iterations of this
framework tried sleep-based retries in test code for a flaky-looking "search after create"
pattern; the real fix turned out to be a missing wait condition inside the page objects
(documented below), and those sleeps were removed once the actual cause was found.

A recurring lesson from building this framework against a real application (not a QA training
sandbox) is that **the wait condition matters as much as whether you wait at all**:

- `waitForVisibility(locator).isDisplayed()` only proves an element *exists and is visible* — not
  that it has the data you're waiting for. OrangeHRM's SPA renders an empty page shell immediately
  and streams data in afterward; a naive `isDisplayed()` check after navigating to Personal Details
  reads the empty shell as "done" before the employee's name has actually loaded.
  `WaitUtils.waitForNonEmptyText` / `waitForNonEmptyValue` wait for the *content*, not just the
  element's presence.
- After clicking a results-table Search button, `WaitUtils.waitForFormLoaderToDisappear()` (the
  app's `.oxd-form-loader` overlay) is **not** a reliable "results updated" signal — that overlay
  doesn't reliably appear for a plain list search, only for some create/validate flows. Reading the
  table immediately afterward can land on the stale pre-search row set. The fix used throughout
  this framework's list pages is `waitForTextToBePresent(resultsLocator, query)` — wait for the
  actual searched value to appear in the results, which ties the wait to the real, observable
  post-search state instead of a generic and sometimes-absent loading indicator.
- `WebElement.clear()` does not reliably clear this application's Vue-controlled inputs — it can
  clear the visible DOM text without Vue's `v-model` registering the change, so a subsequent
  `sendKeys()` types into what the framework still considers the old value, producing a corrupted
  concatenation. `ElementActions.typeOverAutoPopulatedValue` replaces `.clear()` with a real
  `Ctrl+A` + `Delete` keystroke sequence for any field that starts with a server-populated default
  (e.g. Add Employee's suggested next Employee Id).
- Headless Chrome's `driver.manage().window().maximize()` has no real display to maximize against
  and silently falls back to a small viewport — which is enough to trip OrangeHRM's
  mobile-responsive breakpoint (collapsed filter panels, card layouts instead of tables).
  `DriverFactory` sets an explicit `--window-size=1920,1080` launch argument (plus a post-launch
  `setSize()` as a second safety net) whenever running headless.

## Locator strategy

OrangeHRM's form fields carry no `id`, `name`, or `aria-label` — the only stable anchor is each
field's visible `<label>` text. Every label-anchored locator in this framework (in `BasePage`,
`DropdownComponent`, `FileUploadComponent`, `AutocompleteComponent`) resolves the field's
containing `.oxd-input-group` via the same XPath idiom, centralized in
`utils.LocatorUtils.INPUT_GROUP_CLASS_PREDICATE`:

```
contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')
```

This looks more verbose than the obvious `contains(@class,'oxd-input-group')`, and that's
deliberate: the naive version is a real bug, not just a style preference. OrangeHRM's DOM has a
child wrapper `class="oxd-input-group__label-wrapper"` sitting between the label and the real
input group — and that class name literally *starts with* `oxd-input-group`, so a plain substring
`contains()` check matches the wrapper (which has no input inside it) before it ever reaches the
real group. The padded, space-delimited version matches the class as a whole token, the same way a
CSS class selector would. This was found by comparing `document.evaluate()` (real XPath) against
`element.closest()` (real CSS-token matching) directly against the live DOM — they disagreed, and
only the XPath version was wrong.

Wherever more than one instance of the same widget can exist on a page (Employee List has both an
"Employee Name" and a "Supervisor Name" autocomplete field), the locator is scoped to the specific
field just interacted with rather than searching the whole document for "the first match of this
widget type anywhere" — an unscoped locator is a latent bug on any page with two or more instances
of that widget, even if it happens to work when only one instance is ever open during manual
testing.

Row-action icon buttons (view/edit/delete) have **no consistent position** across OrangeHRM's own
tables — Pay Grades and Claims render delete-then-edit, Recruitment Candidates render
view-then-delete. Every such locator resolves the specific action by its icon class
(`bi-eye-fill`, `bi-trash`, ...), never by button index.

## Checkboxes render invisibly

OrangeHRM's checkboxes (bulk row-select, Recruitment's "Consent to keep data") render the real
`<input type="checkbox">` with `opacity: 0` and show a styled sibling `<span>` for the visible
appearance — confirmed via `getComputedStyle`. Selenium correctly refuses to click an element it
can't see. `ElementActions.setCheckbox` and `components.BulkCheckboxComponent` click the enclosing,
visible `<label>` (which natively toggles the input) while still reading `.isSelected()` from the
actual `<input>` via a *presence*-based wait rather than a visibility-based one — reading a DOM
boolean property never required the element to be visually displayed in the first place.

## Reporting

`reports.ExtentManager` owns a single shared `ExtentReports` instance plus a
`ThreadLocal<ExtentTest>` (same rationale as the driver/asserts pattern above).
`listeners.TestNGListener`, registered once in each suite XML's `<listeners>` block, creates one
`ExtentTest` per test method, logs pass/fail/skip, and flushes the report at suite end.
`assertion.HardAsserts` / `SoftAsserts` log every individual assertion (not just the final
pass/fail) to the same report, and `utils.ScreenshotUtils` attaches a screenshot automatically on
any failure — both the assertion-level catch block and `TestNGListener.onTestFailure` capture one,
so a failure is never left without visual evidence.

Passing tests get visual evidence too, not just a text log line. `ElementActions.captureEvidence`
(exposed per-page as named methods, e.g. `PersonalDetailsPage.captureFullNameEvidence()`,
`UserListPage.captureUsernameEvidence()`) takes a full-page screenshot with the specific validated
element outlined via a temporary inline `style` attribute (never a CSS class, so it can't touch the
application's own stylesheet, and it's always restored in a `finally` block regardless of outcome),
then attaches it to that test's ExtentReports entry. This is wired into the five "record now
genuinely exists in the UI" assertions where a screenshot proves something a text log line alone
can't - e.g. after Add Employee, the created employee's exact name outlined on Personal Details;
after Add User, the new username's row outlined in the System Users list. It's deliberately not
applied to every assertion in the suite - a validation-message check or a boolean state check has
nothing meaningful to point at with an outline, so evidence capture is reserved for the assertions
where "here's the actual pixel" is genuinely more convincing than "here's the pass/fail text."

## Data-driven testing

`utils.ExcelUtils.readSheet` reads a classpath `.xlsx` resource into a list of header-keyed row
maps via Apache POI. `testdata.TestDataProvider` exposes this as a TestNG `@DataProvider`
(`employeeData`), consumed by `DataDrivenEmployeeTests`. Each row supplies a *base* name;
`utils.TestDataUtils.uniqueValue` appends a timestamp-derived suffix at runtime so the same
spreadsheet can be replayed indefinitely against the shared demo without id/name collisions.

## Extensibility

To add a new page:
1. Add `elements/<module>/XxxPageElements.java` extending `BasePage` — every `By` locator for that
   page as a `protected final` field, using the label-anchored locator helpers, and nothing else.
   Verify every locator against the live DOM before writing it, never assume.
2. Add `pages/<module>/XxxPage.java extends XxxPageElements` — every action method, composing
   `components/*` for any dropdown/autocomplete/file-upload/checkbox fields. No locator belongs
   here; reference the inherited fields by name.
3. Add a test class under `tests/<module>/` extending `AuthenticatedBaseTest` (or `BaseTest`
   directly if the test is about the login flow itself).
4. Register the class in `testng.xml` (and `smoke.xml`/`regression.xml` if appropriate).

Parallel execution, additional browsers, and additional environments (`qa.properties`-style
overrides) are supported by the existing `ThreadLocal` driver/asserts pattern and
`ConfigReader`'s `-D`-overridable properties without further structural change.

## Known technical debt

- `listeners.RetryAnalyzer` (a bounded single retry) is attached via
  `@Test(retryAnalyzer = RetryAnalyzer.class)` to the five PIM test methods that route through
  Employee List → Add Employee or the bulk-checkbox interaction
  (`EmployeeListTests.searchByEmployeeNameReturnsMatchingEmployee`,
  `EmployeeListTests.bulkRowSelectCheckboxesTrackIndependently`, both `AddEmployeeTests` methods,
  `DataDrivenEmployeeTests.employeeFromExcelRowIsCreatedSuccessfully`) — a deliberate, evidence-based
  decision, not a default-on blanket policy. Across repeated full-suite runs against the live public
  demo, these were the only methods observed to fail, each time non-deterministically: the same
  `clickAdd()` call succeeded on 3 of 5 data-driven rows in one run before failing on the 4th with
  identical code and data shape, and one run logged a raw `java.net.SocketException: Connection
  reset` from the WebDriver/CDP channel mid-suite. That pattern — identical code path passing
  repeatedly then failing once, alongside an observed transport-level reset — is the signature of
  live-server/network noise, not a locator or wait-condition defect; every *deterministic* flakiness
  root cause found while building this suite was fixed properly instead of masked (see the
  wait-strategy section above). Every other test method in the suite still runs with no retry —
  a genuine failure there surfaces as a failure, on the first attempt.
- `utils.TestDataUtils`'s uniqueness scheme (`System.currentTimeMillis()`-derived suffix) is
  sufficient for this suite's execution pattern (tests run seconds apart, never in true parallel
  yet) but would need a stronger scheme (e.g. `UUID`-based) if parallel execution is turned on.

# CRM_Assessment — OrangeHRM Selenium Automation Framework

Enterprise-style Selenium + Java + TestNG + Maven automation framework built for an SDET
assessment, targeting the public [OrangeHRM OS 5.9 demo](https://opensource-demo.orangehrmlive.com/).

## Application Under Test

- **App**: OrangeHRM Open Source, version OS 5.9
- **URL**: https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
- **Credentials**: `Admin` / `admin123` (publicly documented on the login page itself)
- **Nature**: a shared, publicly-writable sandbox used concurrently by many testers worldwide — see
  [docs/test-strategy.md](docs/test-strategy.md#known-limitations) for how the framework handles that.

## Tech stack

| Concern | Choice |
|---|---|
| Language | Java 17 |
| Browser automation | Selenium WebDriver 4.23.0 |
| Test runner | TestNG 7.10.2 |
| Build | Maven (Surefire runs the TestNG suite) |
| Reporting | ExtentReports 5.1.1 (Spark reporter, HTML) |
| Test data | Apache POI 5.4.0 (Excel) |
| Driver management | WebDriverManager (bonigarcia) — no manual driver binaries |

## Project structure

```
src/main/java/com/crmassessment/
  config/        ConfigReader — all environment values, no hardcoding
  driver/        DriverFactory, DriverManager, BrowserType — ThreadLocal WebDriver lifecycle
  elements/      Locators only, one *PageElements class per page, mirrored by module (admin, pim, leave, recruitment)
  pages/         Page Object Model — action methods only, one *Page class per page (extends its *PageElements)
  components/    Reusable widget drivers shared across pages (dropdown, autocomplete, file upload, bulk checkboxes)
  utils/         ElementActions, WaitUtils, ExcelUtils, FileUtils, TestDataUtils, LocatorUtils
  assertion/     Hard/soft assertion facade, integrated with ExtentReports
  listeners/     TestNGListener (ExtentReports lifecycle), RetryAnalyzer
  reports/       ExtentManager

src/test/java/com/crmassessment/
  base/          BaseTest, AuthenticatedBaseTest
  tests/         Test classes, one package per module + endtoend/
  testdata/      EmployeeTestData, TestDataProvider (Excel-backed @DataProvider)

src/test/resources/
  config/        config.properties (environment values)
  suites/        testng.xml (full suite), smoke.xml, regression.xml
  testdata/      employeeTestData.xlsx (data-driven test source)

test-data/
  excel/         manual-test-cases.xlsx — 30 manual test cases
  files/         upload/ (resume fixtures), expected-downloads/ (Chrome download target)

docs/
  test-strategy.md            scope, approach, coverage, known limitations
  framework-architecture.md   design patterns, wait/locator strategy, extensibility
  requirement-traceability.xlsx   assignment requirement → test case mapping

reports/         generated ExtentReports HTML (gitignored contents, kept via .gitkeep)
screenshots/     generated failure screenshots (gitignored contents, kept via .gitkeep)
```

## Prerequisites

- Java 17 (JDK)
- Google Chrome installed locally (Firefox/Edge also supported — see below)
- Maven 3.x — or nothing at all: this project ships the Maven Wrapper (`mvnw` / `mvnw.cmd`), which
  downloads and runs the correct Maven version on its own. Substitute `./mvnw` (`.\mvnw.cmd` on
  Windows) for `mvn` in every command below if you don't have Maven installed.

## Running the tests

Full regression suite, visible browser:

```bash
mvn clean test
```

Headless (no visible browser window):

```bash
mvn clean test -Dtest.headless=true
```

Different browser:

```bash
mvn clean test -Dtest.browser=firefox
```

Smoke suite only (critical path: login/logout + one Add Employee happy path — 4 tests):

```bash
mvn clean test -Dtest.suiteXmlFile=src/test/resources/suites/smoke.xml
```

Regression suite explicitly (identical content to the default `testng.xml`):

```bash
mvn clean test -Dtest.suiteXmlFile=src/test/resources/suites/regression.xml
```

No separate setup step is required — WebDriverManager downloads the matching ChromeDriver
binary automatically on first run.

## Configuration

All environment values live in [src/test/resources/config/config.properties](src/test/resources/config/config.properties)
— base URL, credentials, timeouts, file paths, download directory. Every value can be overridden
per-run via a matching `-D` system property without editing the file (e.g. `-Dbrowser=firefox`).

## Test data

- **Manual test cases**: [test-data/excel/manual-test-cases.xlsx](test-data/excel/manual-test-cases.xlsx) — 30 cases across
  Authentication, PIM, Admin, Leave, and Recruitment, each mapped to an assignment requirement ID.
- **Requirement traceability**: [docs/requirement-traceability.xlsx](docs/requirement-traceability.xlsx) — generated
  directly from the manual test case sheet, so it can't drift out of sync; also includes an
  assignment-requirement compliance checklist.
- **Data-driven source**: [src/test/resources/testdata/employeeTestData.xlsx](src/test/resources/testdata/employeeTestData.xlsx),
  read via `ExcelUtils` + a TestNG `@DataProvider`.

## Reports

Every run generates a timestamped HTML report at `reports/ExtentReport_<timestamp>.html`, including
per-test pass/fail status, step-level assertion messages, and a screenshot attached automatically
on every failure (`screenshots/Failure_<timestamp>.png`).

## Automation status

15 automated test cases (19 executions counting the 5 data-driven rows as separate runs). See
[docs/test-strategy.md](docs/test-strategy.md) for the full manual/automated breakdown and known
limitations of testing against a public, shared demo environment, including the small set of tests
that carry a bounded retry for observed live-network flakiness.

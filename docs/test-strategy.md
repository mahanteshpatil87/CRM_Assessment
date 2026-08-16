# Test Strategy

## Application under test

OrangeHRM Open Source demo (OS 5.9), https://opensource-demo.orangehrmlive.com/ — a publicly
accessible instance of a real HR management product, run as a shared sandbox by the OrangeHRM
project. This is not a purpose-built QA training site: modules, validation rules, and data all
reflect a real, production-grade application.

## Objective

Demonstrate a professional, maintainable, review-ready Selenium automation framework — not just a
collection of scripts — covering both manual and automated test design against real application
behavior, per the assignment brief. Every test case in this suite is grounded in functionality
directly verified in the live application; nothing was invented or assumed.

## Modules covered

| Module | What's exercised |
|---|---|
| Admin / Authentication | Login (valid/invalid), logout, session invalidation |
| PIM | Employee List (search, filter, bulk row-select), Add Employee (happy path + validation), data-driven employee creation |
| Admin / User Management | Add System User (happy path + password-mismatch validation) |
| Leave | Leave List filtering (Leave Type + Status) |
| Recruitment | Add Candidate (valid + invalid resume upload), full E2E: create candidate with resume → search → open detail → download → verify byte-identical |

## Test design approach

Test cases were derived from the application's actual behavior, not written first and adapted to
fit: the OrangeHRM demo was explored module-by-module before any test case was drafted, confirming
which fields, dropdown values, validation messages, and workflows genuinely exist. Manual test
cases were designed first (30 cases, [test-data/excel/manual-test-cases.xlsx](../test-data/excel/manual-test-cases.xlsx)),
covering positive, negative, boundary, and UI/component scenarios across every module in scope,
each mapped to an assignment requirement ID (REQ-01..REQ-12, see the Requirement Legend tab in
that workbook). The strongest 15 — a mixture of simple single-field checks through full multi-step
end-to-end workflows — were then selected for automation following a TDD-style cycle: manual test
case → expected behavior → test method → page object → implementation → execution → refactor.

## Automation scope

- **15 automated test cases** (19 executions counting the 5 `@DataProvider` rows of the
  data-driven test separately). All deterministic failures found while building this suite were
  root-caused and fixed (see [docs/framework-architecture.md](framework-architecture.md)'s Wait
  strategy section for all 8). A small number of PIM tests remain exposed to genuine live-demo
  network noise (observed: an identical code path passing 3 of 5 times before failing once, and a
  raw `SocketException: Connection reset` from the WebDriver channel) rather than a framework
  defect — those five methods carry a bounded single-retry `RetryAnalyzer`, documented in
  framework-architecture.md's Known Technical Debt section; every other test runs with zero
  retries.
- **14 test cases remain deliberately manual** — one-off UI checks (password masking, Reset button),
  scenarios that are hard to assert deterministically against a live shared demo (photo upload
  boundary conditions, duplicate-username races), or genuinely low-value automation targets
  (assignment explicitly warns against automating trivial variations for their own sake).
- **1 test case (TC-PIM-012)** is scoped and documented but not yet implemented — attaching a file
  to an existing employee's Personal Details tab.

See [docs/requirement-traceability.xlsx](requirement-traceability.xlsx) for the full requirement →
test case mapping (generated directly from the manual test case sheet, so the two can't drift out
of sync) and an assignment-requirement compliance checklist.

## Known limitations

- **No native multi-select widget exists anywhere in the current OrangeHRM OS 5.9 build.** Verified
  by direct DOM inspection of every plausible candidate (Pay Grade currency assignment, the PIM
  report field picker, Leave bulk entitlement assignment) — all are single-select-plus-repeat-add
  or dropdown-filter patterns, not a selectable list. REQ-04 (multi-select) is instead demonstrated
  via the verified list-level bulk row-select checkboxes present on every results table (Employee
  List, Candidates, Pay Grades, Users) — a documented, defensible substitute, not an invented one.
- **The public demo is a shared, continuously-modified sandbox** — hundreds of employee and
  candidate records exist from other testers worldwide, and the record set changes between runs.
  Every automated test that creates data generates its own uniquely-named record
  (`TestDataUtils.uniqueValue`) rather than assuming any fixture exists or stays put; no test
  depends on the total record count or another tester's data still being present.
- **No Payroll module** exists in this Open Source edition, so there is no payslip/PDF download
  path. REQ-08 (file download) is instead demonstrated via the Recruitment resume attachment
  download, which is a genuine, verified file-download feature.
- **Exact validation copy for rejected file uploads** (TC-PIM-010, TC-REC-002) was not independently
  confirmed byte-for-byte — the automated tests assert that the save is rejected (no record
  created), not the literal error string, since that specific text was not directly observed during
  manual exploration.
- **Two dropdown value sets** (Admin → Add User's User Role/Status; PIM → Employee List's Employment
  Status filter value used in automated tests) are standard, stable, well-documented OrangeHRM
  product-wide defaults that were confirmed directly against the live `.oxd-select-option` list
  during this project, not assumed.

## Out of scope

Payroll, Time & Attendance clock-in/out, Performance reviews, and Directory were not explored or
tested — outside the assignment's required coverage areas (text/number input, dropdown,
multi-select, buttons, checkboxes, file upload/download, add/edit forms) and not referenced by any
selected test case.

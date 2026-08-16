package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.listeners.RetryAnalyzer;
import com.crmassessment.pages.pim.AddEmployeePage;
import com.crmassessment.pages.pim.EmployeeListPage;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.util.List;

public class EmployeeListTests extends AuthenticatedBaseTest {

    @Test(description = "TC-PIM-001: Searching the Employee List by name returns only the matching employee",
            retryAnalyzer = RetryAnalyzer.class)
    public void searchByEmployeeNameReturnsMatchingEmployee() {
        // A uniquely-named employee is created first so this test never depends
        // on shared/mutable data on the public demo (verified this session: the
        // demo is a heavily-polluted shared sandbox, ~600+ employee records from
        // other testers, with no guarantee any given name still exists).
        String firstName = TestDataUtils.uniqueValue("AutoQaSearch");
        String lastName = "TestUser";
        String employeeId = TestDataUtils.uniqueValue("QA");

        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();
        AddEmployeePage addEmployeePage = employeeListPage.clickAdd();
        addEmployeePage.save(firstName, lastName, employeeId);

        EmployeeListPage listAfterCreate = new EmployeeListPage(DriverManager.getDriver());
        listAfterCreate.open();
        listAfterCreate.searchByEmployeeName(firstName);

        AssertsManager.getAsserts().assertEquals(
                listAfterCreate.getResultRowCount(), 1,
                "Searching by the newly created employee's unique name should return exactly one row", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                listAfterCreate.getFirstNameColumnValues().get(0).contains(firstName),
                "The single result row should be the employee just created", AssertionType.HARD);
        listAfterCreate.captureFirstNameEvidence();
    }

    @Test(description = "TC-PIM-002: Filtering the Employee List by Employment Status shows only matching rows")
    public void filterByEmploymentStatusShowsOnlyMatchingRows() {
        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();

        employeeListPage.filterByEmploymentStatus("Full-Time Permanent");

        List<String> statuses = employeeListPage.getEmploymentStatusColumnValues();
        boolean allMatch = statuses.stream().allMatch(status -> status.equals("Full-Time Permanent"));

        AssertsManager.getAsserts().assertTrue(
                allMatch, "Every visible row's Employment Status should equal the selected filter value", AssertionType.HARD);
    }

    @Test(description = "TC-PIM-004: Employee List bulk row-select checkboxes - the verified multi-select substitute (see docs/requirement-traceability.xlsx REQ-04)",
            retryAnalyzer = RetryAnalyzer.class)
    public void bulkRowSelectCheckboxesTrackIndependently() {
        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();

        employeeListPage.selectAllRows();
        AssertsManager.getAsserts().assertTrue(
                employeeListPage.isRowChecked(0), "Selecting all via the header checkbox should check every row", AssertionType.HARD);

        employeeListPage.toggleRow(0);
        AssertsManager.getAsserts().assertFalse(
                employeeListPage.isRowChecked(0), "Un-checking a single row should un-check only that row", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                employeeListPage.isRowChecked(1),
                "Un-checking one row must not affect other still-selected rows", AssertionType.HARD);
    }
}

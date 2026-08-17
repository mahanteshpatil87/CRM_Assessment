package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.listeners.RetryAnalyzer;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.util.List;

public class EmployeeListTests extends AuthenticatedBaseTest {

    @Test(description = "TC-PIM-001: Searching the Employee List by name returns only the matching employee",
            retryAnalyzer = RetryAnalyzer.class)
    public void searchByEmployeeNameReturnsMatchingEmployee() {
        // The searched name must still be unique so this test never depends
        // on shared/mutable data on the public demo - it's a heavily-polluted
        // shared sandbox with ~600+ employee records from other testers, no
        // guarantee any given name still exists. That uniqueness is carried
        // on the last name only, so the first name stays a plain, real-looking
        // name rather than "AutoQaSearch784512".
        String[] name = TestDataUtils.randomNamePair();
        String firstName = name[0];
        String lastName = TestDataUtils.uniqueValue(name[1]);
        String employeeId = TestDataUtils.uniqueValue("QA");

        pages.employeeListPage
                .open();

        pages.employeeListPage
                .clickAdd();

        pages.addEmployeePage
                .save(firstName, lastName, employeeId);

        pages.employeeListPage
                .open();

        pages.employeeListPage
                .searchByEmployeeName(lastName);

        AssertsManager.getAsserts().assertEquals(
                pages.employeeListPage.getResultRowCount(), 1,
                "Searching by the newly created employee's unique last name should return exactly one row", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                pages.employeeListPage.getLastNameColumnValues().get(0).contains(lastName),
                "The single result row should be the employee just created", AssertionType.HARD);

        pages.employeeListPage
                .captureFirstNameEvidence();
    }

    @Test(description = "TC-PIM-002: Filtering the Employee List by Employment Status shows only matching rows")
    public void filterByEmploymentStatusShowsOnlyMatchingRows() {
        pages.employeeListPage
                .open();

        pages.employeeListPage
                .filterByEmploymentStatus("Full-Time Permanent");

        List<String> statuses = pages.employeeListPage.getEmploymentStatusColumnValues();
        boolean allMatch = statuses.stream().allMatch(status -> status.equals("Full-Time Permanent"));

        AssertsManager.getAsserts().assertTrue(
                allMatch, "Every visible row's Employment Status should equal the selected filter value", AssertionType.HARD);

        pages.employeeListPage
                .captureEmploymentStatusEvidence();
    }

    @Test(description = "TC-PIM-004: Employee List bulk row-select checkboxes - the verified multi-select substitute (see docs/requirement-traceability.xlsx REQ-04)",
            retryAnalyzer = RetryAnalyzer.class)
    public void bulkRowSelectCheckboxesTrackIndependently() {
        pages.employeeListPage
                .open();

        pages.employeeListPage
                .selectAllRows();
        AssertsManager.getAsserts().assertTrue(
                pages.employeeListPage.isRowChecked(0), "Selecting all via the header checkbox should check every row", AssertionType.HARD);

        pages.employeeListPage
                .toggleRow(0);
        AssertsManager.getAsserts().assertFalse(
                pages.employeeListPage.isRowChecked(0), "Un-checking a single row should un-check only that row", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                pages.employeeListPage.isRowChecked(1),
                "Un-checking one row must not affect other still-selected rows", AssertionType.HARD);

        pages.employeeListPage
                .captureRowCheckboxEvidence(1, "Row 1 still checked after un-checking row 0");
    }
}

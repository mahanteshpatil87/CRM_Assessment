package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.listeners.RetryAnalyzer;
import com.crmassessment.pages.pim.AddEmployeePage;
import com.crmassessment.pages.pim.EmployeeListPage;
import com.crmassessment.pages.pim.PersonalDetailsPage;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

public class AddEmployeeTests extends AuthenticatedBaseTest {

    @Test(description = "TC-PIM-006: Adding an employee with only the mandatory name fields succeeds and lands on Personal Details",
            retryAnalyzer = RetryAnalyzer.class)
    public void addEmployeeWithMandatoryFieldsSucceeds() {
        // This test only reads back the record it just created (direct
        // navigation to its own Personal Details page, not a name search),
        // so the name itself never needs to be unique against the shared
        // demo's other records - only Employee Id does (see below).
        String firstName = TestDataUtils.randomFirstName();
        String lastName = "TestUser";
        // The pre-filled default Employee Id can collide with another tester's
        // record on this shared demo (verified: produces "Employee Id already
        // exists") - always supply a unique one explicitly.
        String employeeId = TestDataUtils.uniqueValue("QA");

        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();
        AddEmployeePage addEmployeePage = employeeListPage.clickAdd();

        PersonalDetailsPage personalDetailsPage = addEmployeePage.save(firstName, lastName, employeeId);

        AssertsManager.getAsserts().assertTrue(
                personalDetailsPage.isDisplayed(),
                "Save should navigate to the new employee's Personal Details page", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                personalDetailsPage.getEmployeeFullName(), firstName + " " + lastName,
                "Personal Details should show the exact name just entered", AssertionType.HARD);
        personalDetailsPage.captureFullNameEvidence();
    }

    @Test(description = "TC-PIM-007: Submitting Add Employee with First/Last Name blank is blocked by validation",
            retryAnalyzer = RetryAnalyzer.class)
    public void addEmployeeWithBlankMandatoryFieldsIsBlocked() {
        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();
        AddEmployeePage addEmployeePage = employeeListPage.clickAdd();

        addEmployeePage.clickSave();

        AssertsManager.getAsserts().assertEquals(
                addEmployeePage.getValidationMessageCount(), 2,
                "Both First Name and Last Name should show a \"Required\" validation message", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                addEmployeePage.isStillOnAddEmployeePage(),
                "A blocked save must not navigate away from Add Employee", AssertionType.HARD);
    }
}

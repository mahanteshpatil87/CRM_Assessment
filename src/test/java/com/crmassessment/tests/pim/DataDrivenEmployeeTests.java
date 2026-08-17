package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.listeners.RetryAnalyzer;
import com.crmassessment.pages.pim.AddEmployeePage;
import com.crmassessment.pages.pim.EmployeeListPage;
import com.crmassessment.pages.pim.PersonalDetailsPage;
import com.crmassessment.testdata.EmployeeTestData;
import com.crmassessment.testdata.TestDataProvider;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

public class DataDrivenEmployeeTests extends AuthenticatedBaseTest {

    @Test(dataProvider = "employeeData", dataProviderClass = TestDataProvider.class,
            description = "TC-PIM-013: Each employee row from the Excel test-data source is created and lands correctly in Employee List",
            retryAnalyzer = RetryAnalyzer.class)
    public void employeeFromExcelRowIsCreatedSuccessfully(EmployeeTestData employeeTestData) {
        // This test only reads back the record it just created (direct
        // navigation to its own Personal Details page, not a name search),
        // so the Excel row's name is used exactly as-is - the same
        // spreadsheet can be replayed indefinitely regardless, since it's
        // Employee Id (below) that the app actually requires to be unique.
        String firstName = employeeTestData.firstName();
        String employeeId = TestDataUtils.uniqueValue("QA");

        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();
        AddEmployeePage addEmployeePage = employeeListPage.clickAdd();

        PersonalDetailsPage personalDetailsPage = addEmployeePage.save(firstName, employeeTestData.lastName(), employeeId);

        AssertsManager.getAsserts().assertEquals(
                personalDetailsPage.getEmployeeFullName(), firstName + " " + employeeTestData.lastName(),
                "Employee created from Excel row (" + employeeTestData.firstName() + " " + employeeTestData.lastName()
                        + ") should show the exact generated name on Personal Details", AssertionType.HARD);
        personalDetailsPage.captureFullNameEvidence();
    }
}

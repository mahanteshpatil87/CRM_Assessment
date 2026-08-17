package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.listeners.RetryAnalyzer;
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
        String driversLicenseNumber = TestDataUtils.randomDriversLicenseNumber();
        String licenseExpiryDate = TestDataUtils.randomLicenseExpiryDate();
        String maritalStatus = TestDataUtils.randomMaritalStatus();
        String dateOfBirth = TestDataUtils.randomDateOfBirth();
        String gender = TestDataUtils.randomGender();
        String bloodType = TestDataUtils.randomBloodType();
        String testField = TestDataUtils.uniqueValue("QA");

        pages.employeeListPage
                .open();

        pages.employeeListPage
                .clickAdd();

        pages.addEmployeePage
                .save(firstName, employeeTestData.lastName(), employeeId);

        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getEmployeeFullName(), firstName + " " + employeeTestData.lastName(),
                "Employee created from Excel row (" + employeeTestData.firstName() + " " + employeeTestData.lastName()
                        + ") should show the exact generated name on Personal Details", AssertionType.HARD);

        pages.personalDetailsPage
                .captureFullNameEvidence();

        // Beyond Name/Employee Id, Personal Details also carries demographic
        // and identity fields (Date of Birth, Nationality, etc.) that Add
        // Employee's own quick-create form doesn't expose - fill and verify
        // those here rather than leaving them untested.
        pages.personalDetailsPage
                .fillAdditionalDetails(driversLicenseNumber, licenseExpiryDate, "Indian", maritalStatus,
                        dateOfBirth, gender, bloodType, testField);

        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getDateOfBirth(), dateOfBirth,
                "Personal Details should show the exact Date of Birth just entered", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getGender(), gender,
                "Personal Details should show the exact Gender just selected", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getBloodType(), bloodType,
                "Custom Fields should show the exact Blood Type just selected", AssertionType.HARD);

        pages.personalDetailsPage
                .captureAdditionalDetailsEvidence();
    }
}

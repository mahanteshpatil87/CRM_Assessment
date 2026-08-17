package com.crmassessment.tests.pim;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.listeners.RetryAnalyzer;
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
        String[] name = TestDataUtils.randomNamePair();
        String firstName = name[0];
        String lastName = name[1];
        // The pre-filled default Employee Id can collide with another tester's
        // record on this shared demo (verified: produces "Employee Id already
        // exists") - always supply a unique one explicitly.
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
                .save(firstName, lastName, employeeId);

        AssertsManager.getAsserts().assertTrue(
                pages.personalDetailsPage.isDisplayed(),
                "Save should navigate to the new employee's Personal Details page", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getEmployeeFullName(), firstName + " " + lastName,
                "Personal Details should show the exact name just entered", AssertionType.HARD);

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
                pages.personalDetailsPage.getNationality(), "Indian",
                "Personal Details should show the exact Nationality just selected", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getMaritalStatus(), maritalStatus,
                "Personal Details should show the exact Marital Status just selected", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getDateOfBirth(), dateOfBirth,
                "Personal Details should show the exact Date of Birth just entered", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getGender(), gender,
                "Personal Details should show the exact Gender just selected", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getBloodType(), bloodType,
                "Custom Fields should show the exact Blood Type just selected", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getDriversLicenseNumber(), driversLicenseNumber,
                "Personal Details should show the exact Driver's License Number just entered", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getLicenseExpiryDate(), licenseExpiryDate,
                "Personal Details should show the exact License Expiry Date just entered", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.personalDetailsPage.getTestField(), testField,
                "Custom Fields should show the exact Test_Field value just entered", AssertionType.HARD);

        pages.personalDetailsPage
                .captureAdditionalDetailsEvidence();
    }

    @Test(description = "TC-PIM-007: Submitting Add Employee with First/Last Name blank is blocked by validation",
            retryAnalyzer = RetryAnalyzer.class)
    public void addEmployeeWithBlankMandatoryFieldsIsBlocked() {
        pages.employeeListPage
                .open();

        pages.employeeListPage
                .clickAdd();

        pages.addEmployeePage
                .clickSave();

        AssertsManager.getAsserts().assertEquals(
                pages.addEmployeePage.getValidationMessageCount(), 2,
                "Both First Name and Last Name should show a \"Required\" validation message", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                pages.addEmployeePage.isStillOnAddEmployeePage(),
                "A blocked save must not navigate away from Add Employee", AssertionType.HARD);

        pages.addEmployeePage
                .captureValidationEvidence();
    }
}

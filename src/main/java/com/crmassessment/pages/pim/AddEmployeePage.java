package com.crmassessment.pages.pim;

import com.crmassessment.elements.pim.AddEmployeePageElements;
import org.openqa.selenium.WebDriver;

public class AddEmployeePage extends AddEmployeePageElements {

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public AddEmployeePage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        return this;
    }

    public AddEmployeePage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        return this;
    }

    /**
     * The Employee Id field is pre-filled with a server-suggested value that
     * can collide with another user's record on the shared public demo
     * (verified: saving with the default value can fail with "Employee Id
     * already exists"). Callers should always pass their own unique id
     * rather than relying on the pre-filled default.
     */
    public AddEmployeePage enterEmployeeId(String employeeId) {
        typeOverAutoPopulatedValue(employeeIdInput, employeeId);
        return this;
    }

    public AddEmployeePage clickSave() {
        // The page's own initial async setup (employee-id suggestion, photo
        // config, dropdown data) can still be in flight if Save is clicked
        // immediately after navigating here without any prior interaction
        // (e.g. the blank-mandatory-fields validation test) - verified this
        // causes an ElementClickIntercepted on the loader overlay. Waiting
        // for Employee Id's async default to land is a reliable proxy for
        // "this page has finished its initial load", regardless of whether
        // the caller is about to overwrite that value or not.
        waitForNonEmptyValue(employeeIdInput);
        waitForFormLoaderToDisappear();
        click(saveButton);
        return this;
    }

    public PersonalDetailsPage save(String firstName, String lastName, String uniqueEmployeeId) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmployeeId(uniqueEmployeeId);
        clickSave();
        // Confirm the save's navigation actually completed before returning -
        // callers that immediately navigate elsewhere (e.g. straight back to
        // Employee List to search) can otherwise race the still-in-flight
        // save request.
        waitForUrlMatches(".*/viewPersonalDetails.*");
        return new PersonalDetailsPage(driver);
    }

    public int getValidationMessageCount() {
        isDisplayed(validationMessages); // wait for the first message to render before counting - avoids a race right after clickSave()
        return countElements(validationMessages);
    }

    public boolean isStillOnAddEmployeePage() {
        return driver.getCurrentUrl().contains("addEmployee");
    }

    /** PASS-evidence for "the blank-field validation actually appears" - attaches a screenshot with the message outlined. */
    public AddEmployeePage captureValidationEvidence() {
        captureEvidence(validationMessages, "Required-field validation visible on Add Employee");
        return this;
    }
}

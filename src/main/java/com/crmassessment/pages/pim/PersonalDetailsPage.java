package com.crmassessment.pages.pim;

import com.crmassessment.components.DropdownComponent;
import com.crmassessment.elements.pim.PersonalDetailsPageElements;
import org.openqa.selenium.WebDriver;

public class PersonalDetailsPage extends PersonalDetailsPageElements {

    private final DropdownComponent dropdown;

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
        this.dropdown = new DropdownComponent(driver);
    }

    public String getEmployeeFullName() {
        waitForNonEmptyText(employeeFullNameHeading);
        return getText(employeeFullNameHeading);
    }

    /** PASS-evidence for "the employee was actually created" - attaches a screenshot with the visible name outlined. */
    public PersonalDetailsPage captureFullNameEvidence() {
        waitForNonEmptyText(employeeFullNameHeading);
        captureEvidence(employeeFullNameHeading, "New employee's name visible on Personal Details page");
        return this;
    }

    public PersonalDetailsPage enterDriversLicenseNumber(String driversLicenseNumber) {
        type(driversLicenseNumberInput, driversLicenseNumber);
        return this;
    }

    // OrangeHRM's own placeholder reads "yyyy-dd-mm" (day before month, not
    // the usual yyyy-mm-dd) - verified live, both here and on Date of Birth.
    public PersonalDetailsPage enterLicenseExpiryDate(String licenseExpiryDateYyyyDdMm) {
        type(licenseExpiryDateInput, licenseExpiryDateYyyyDdMm);
        return this;
    }

    public PersonalDetailsPage selectNationality(String nationality) {
        dropdown.selectByLabel("Nationality", nationality);
        return this;
    }

    public PersonalDetailsPage selectMaritalStatus(String maritalStatus) {
        dropdown.selectByLabel("Marital Status", maritalStatus);
        return this;
    }

    public PersonalDetailsPage enterDateOfBirth(String dateOfBirthYyyyDdMm) {
        type(dateOfBirthInput, dateOfBirthYyyyDdMm);
        return this;
    }

    /**
     * OrangeHRM renders the real &lt;input type="radio"&gt; with opacity:0
     * and a styled sibling label instead (verified live - the same pattern
     * as ElementActions.setCheckbox's checkboxes), so the click is
     * dispatched to the enclosing, visible &lt;label&gt; rather than the
     * radio input itself.
     */
    public PersonalDetailsPage selectGender(String gender) {
        click("Male".equalsIgnoreCase(gender) ? genderMaleLabel : genderFemaleLabel);
        return this;
    }

    /**
     * Date of Birth/Gender/Nationality/etc. live in their own form with
     * their own Save button, independent of Blood Type/Test_Field's
     * "Custom Fields" form (see personalDetailsSaveButton's locator note) -
     * this only saves the main form.
     */
    public PersonalDetailsPage clickSavePersonalDetails() {
        click(personalDetailsSaveButton);
        waitForFormLoaderToDisappear();
        return this;
    }

    public PersonalDetailsPage selectBloodType(String bloodType) {
        dropdown.selectByLabel("Blood Type", bloodType);
        return this;
    }

    public PersonalDetailsPage enterTestField(String value) {
        type(testFieldInput, value);
        return this;
    }

    public PersonalDetailsPage clickSaveCustomFields() {
        click(customFieldsSaveButton);
        waitForFormLoaderToDisappear();
        return this;
    }

    /**
     * Fills every additional Personal Details field beyond Name/Employee Id
     * and saves both of the page's forms: the main form (driver's license,
     * nationality, marital status, date of birth, gender) and the separate
     * Custom Fields form (blood type, test field) - verified live that these
     * are two independent forms/Save buttons on the same page, not one.
     */
    public PersonalDetailsPage fillAdditionalDetails(String driversLicenseNumber, String licenseExpiryDateYyyyDdMm,
            String nationality, String maritalStatus, String dateOfBirthYyyyDdMm, String gender,
            String bloodType, String testField) {
        enterDriversLicenseNumber(driversLicenseNumber);
        enterLicenseExpiryDate(licenseExpiryDateYyyyDdMm);
        selectNationality(nationality);
        selectMaritalStatus(maritalStatus);
        enterDateOfBirth(dateOfBirthYyyyDdMm);
        selectGender(gender);
        clickSavePersonalDetails();
        selectBloodType(bloodType);
        enterTestField(testField);
        clickSaveCustomFields();
        return this;
    }

    public String getDriversLicenseNumber() {
        return getAttribute(driversLicenseNumberInput, "value");
    }

    public String getLicenseExpiryDate() {
        return getAttribute(licenseExpiryDateInput, "value");
    }

    public String getNationality() {
        return dropdown.getSelectedText("Nationality");
    }

    public String getMaritalStatus() {
        return dropdown.getSelectedText("Marital Status");
    }

    public String getDateOfBirth() {
        return getAttribute(dateOfBirthInput, "value");
    }

    public String getGender() {
        return isSelected(genderMaleRadio) ? "Male" : "Female";
    }

    public String getBloodType() {
        return dropdown.getSelectedText("Blood Type");
    }

    public String getTestField() {
        return getAttribute(testFieldInput, "value");
    }

    /** PASS-evidence for "the additional Personal Details fields were actually saved" - attaches a screenshot with Date of Birth outlined. */
    public PersonalDetailsPage captureAdditionalDetailsEvidence() {
        waitForNonEmptyValue(dateOfBirthInput);
        captureEvidence(dateOfBirthInput, "Saved Date of Birth visible on Personal Details page");
        return this;
    }

    public boolean isDisplayed() {
        // A same-SPA navigation (Save -> Personal Details) takes a moment to
        // actually update the URL - checking getCurrentUrl() synchronously
        // right after clickSave() returns can read the still-old URL and
        // report false before the navigation has even happened (verified:
        // getEmployeeFullName(), which doesn't URL-check first and instead
        // just keeps polling for the element/text to appear, was unaffected
        // by this same race - isDisplayed() needs the equivalent wait).
        if (!waitForUrlMatches(".*/viewPersonalDetails.*")) {
            return false;
        }
        try {
            waitForNonEmptyText(employeeFullNameHeading);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}

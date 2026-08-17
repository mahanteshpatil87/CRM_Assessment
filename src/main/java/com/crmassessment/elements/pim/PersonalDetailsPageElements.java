package com.crmassessment.elements.pim;

import com.crmassessment.pages.base.BasePage;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Personal Details page, and nothing else. */
public class PersonalDetailsPageElements extends BasePage {

    protected final By employeeFullNameHeading = By.cssSelector(".orangehrm-edit-employee-name");

    // inputForLabel()'s xpath wraps the label in single quotes, which breaks
    // on this label's own apostrophe (verified live: InvalidSelectorException,
    // "'Driver's License Number'" is not valid XPath) - double-quoted here
    // instead, since the label itself contains no double quote.
    protected final By driversLicenseNumberInput = By.xpath(String.format(
            "//label[normalize-space()=\"Driver's License Number\"]/ancestor::div[%s][1]//input",
            LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    protected final By licenseExpiryDateInput = inputForLabel("License Expiry Date");
    protected final By dateOfBirthInput = inputForLabel("Date of Birth");
    protected final By genderMaleLabel = By.xpath("//label[normalize-space()='Male']");
    protected final By genderMaleRadio = By.xpath("//label[normalize-space()='Male']//input[@type='radio']");
    protected final By genderFemaleLabel = By.xpath("//label[normalize-space()='Female']");
    protected final By genderFemaleRadio = By.xpath("//label[normalize-space()='Female']//input[@type='radio']");
    // Verified against the live app: Date of Birth/Gender/Nationality/etc. sit
    // in their own <form> with their own Save button, and Blood Type/Test_Field
    // (under "Custom Fields") sit in a second, independent <form> with a
    // second Save button of identical markup - each scoped by a label unique
    // to its own form, since "button[type=submit]" alone would match both.
    protected final By personalDetailsSaveButton = By.xpath(
            "//label[normalize-space()='Date of Birth']/ancestor::form[1]//button[@type='submit']");
    protected final By testFieldInput = inputForLabel("Test_Field");
    protected final By customFieldsSaveButton = By.xpath(
            "//label[normalize-space()='Blood Type']/ancestor::form[1]//button[@type='submit']");

    protected PersonalDetailsPageElements(WebDriver driver) {
        super(driver);
    }
}

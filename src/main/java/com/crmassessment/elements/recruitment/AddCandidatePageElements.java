package com.crmassessment.elements.recruitment;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Add Candidate page, and nothing else. */
public class AddCandidatePageElements extends BasePage {

    protected final By firstNameInput = By.cssSelector("input[placeholder='First Name']");
    protected final By lastNameInput = By.cssSelector("input[placeholder='Last Name']");
    protected final By emailInput = inputForLabel("Email");
    protected final By contactNumberInput = inputForLabel("Contact Number");
    protected final By consentCheckbox = By.cssSelector("input[type='checkbox']");
    protected final By saveButton = By.cssSelector("button[type='submit']");
    protected final By validationMessages = By.cssSelector(".oxd-input-group__message");

    protected AddCandidatePageElements(WebDriver driver) {
        super(driver);
    }
}

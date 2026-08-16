package com.crmassessment.elements.admin;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Add User page, and nothing else. */
public class AddUserPageElements extends BasePage {

    protected final By usernameInput = inputForLabel("Username");
    protected final By passwordInput = inputForLabel("Password");
    protected final By confirmPasswordInput = inputForLabel("Confirm Password");
    protected final By confirmPasswordValidationMessage = validationMessageForLabel("Confirm Password");
    protected final By saveButton = By.cssSelector("button[type='submit']");
    protected final By validationMessages = By.cssSelector(".oxd-input-group__message");

    protected AddUserPageElements(WebDriver driver) {
        super(driver);
    }
}

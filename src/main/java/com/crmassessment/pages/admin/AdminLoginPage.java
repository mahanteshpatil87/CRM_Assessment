package com.crmassessment.pages.admin;

import com.crmassessment.elements.admin.AdminLoginPageElements;
import org.openqa.selenium.WebDriver;

public class AdminLoginPage extends AdminLoginPageElements {

    public AdminLoginPage(WebDriver driver) {
        super(driver);
    }

    public AdminLoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public AdminLoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public AdminLoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    public AdminDashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new AdminDashboardPage(driver);
    }

    public boolean isValidationErrorDisplayed() {
        return isDisplayed(errorAlert);
    }

    public String getValidationErrorText() {
        return getText(errorAlert);
    }

    /** PASS-evidence for "the invalid-credentials error actually appears" - attaches a screenshot with the alert outlined. */
    public AdminLoginPage captureValidationErrorEvidence() {
        captureEvidence(errorAlert, "Invalid-credentials error visible on the login page");
        return this;
    }

    /** PASS-evidence for "we're genuinely back on the login page" (e.g. after logout) - attaches a screenshot with the username field outlined. */
    public AdminLoginPage captureLoginFormEvidence() {
        captureEvidence(usernameField, "Login form visible - session no longer authenticated");
        return this;
    }
}

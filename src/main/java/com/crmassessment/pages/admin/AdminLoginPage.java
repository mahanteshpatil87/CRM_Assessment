package com.crmassessment.pages.admin;

import com.crmassessment.elements.admin.AdminLoginPageElements;
import org.openqa.selenium.WebDriver;

public class AdminLoginPage extends AdminLoginPageElements {

    public AdminLoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void clickLogin() {
        click(loginButton);
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
}

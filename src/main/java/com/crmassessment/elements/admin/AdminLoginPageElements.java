package com.crmassessment.elements.admin;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Every locator used on the Admin Login page, and nothing else. Kept
 * separate from the page's action methods (com.crmassessment.pages.admin.AdminLoginPage)
 * so a locator change never touches the file containing the page's behavior, and vice versa.
 */
public class AdminLoginPageElements extends BasePage {

    protected final By usernameField = By.name("username");
    protected final By passwordField = By.name("password");
    protected final By loginButton = By.cssSelector("button.orangehrm-login-button");
    protected final By errorAlert = By.cssSelector(".oxd-alert-content-text");

    protected AdminLoginPageElements(WebDriver driver) {
        super(driver);
    }
}

package com.crmassessment.pages.admin;

import com.crmassessment.elements.admin.AdminDashboardPageElements;
import org.openqa.selenium.WebDriver;

public class AdminDashboardPage extends AdminDashboardPageElements {

    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(breadcrumbHeader) && "Dashboard".equals(getText(breadcrumbHeader));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public AdminLoginPage logout() {
        click(userDropdownTab);
        click(logoutLink);
        return new AdminLoginPage(driver);
    }

    /** PASS-evidence for "the dashboard genuinely loaded after login" - attaches a screenshot with the breadcrumb header outlined. */
    public AdminDashboardPage captureDashboardEvidence() {
        captureEvidence(breadcrumbHeader, "Dashboard breadcrumb visible after a valid login");
        return this;
    }
}

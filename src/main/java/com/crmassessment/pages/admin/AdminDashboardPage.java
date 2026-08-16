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
}

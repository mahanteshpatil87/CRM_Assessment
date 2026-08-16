package com.crmassessment.elements.admin;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Admin Dashboard page, and nothing else. */
public class AdminDashboardPageElements extends BasePage {

    protected final By breadcrumbHeader = By.cssSelector(".oxd-topbar-header-breadcrumb-module");
    protected final By userDropdownTab = By.cssSelector(".oxd-userdropdown-tab");
    protected final By logoutLink = By.xpath("//a[normalize-space()='Logout']");

    protected AdminDashboardPageElements(WebDriver driver) {
        super(driver);
    }
}

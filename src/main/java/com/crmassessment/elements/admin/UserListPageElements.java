package com.crmassessment.elements.admin;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the System Users list page, and nothing else. */
public class UserListPageElements extends BasePage {

    protected final By usernameFilterInput = inputForLabel("Username");
    protected final By searchButton = By.cssSelector("button[type='submit']");
    protected final By addButton = By.xpath("//button[normalize-space()='Add']");
    // Cell order: 0=checkbox, 1=Username, 2=UserRole, 3=EmployeeName, 4=Status, 5=Actions
    protected final By usernameColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(2)");

    protected UserListPageElements(WebDriver driver) {
        super(driver);
    }
}

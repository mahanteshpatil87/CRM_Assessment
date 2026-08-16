package com.crmassessment.elements.pim;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Employee List page, and nothing else. */
public class EmployeeListPageElements extends BasePage {

    protected final By searchButton = By.cssSelector("button[type='submit']");
    protected final By resetButton = By.cssSelector("button[type='reset']");
    protected final By addButton = By.xpath("//button[normalize-space()='Add']");
    protected final By tableRows = By.cssSelector(".oxd-table-body .oxd-table-row");
    // Cell order verified on the live app: 0=checkbox, 1=Id, 2=First(&Middle)Name,
    // 3=LastName, 4=JobTitle, 5=EmploymentStatus, 6=SubUnit, 7=Supervisor, 8=Actions
    protected final By employmentStatusColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(6)");
    protected final By firstNameColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(3)");
    protected final By lastNameColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(4)");

    protected EmployeeListPageElements(WebDriver driver) {
        super(driver);
    }
}

package com.crmassessment.elements.leave;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Leave List page, and nothing else. */
public class LeaveListPageElements extends BasePage {

    protected final By searchButton = By.cssSelector("button[type='submit']");
    protected final By tableRows = By.cssSelector(".oxd-table-body .oxd-table-row");
    // Cell order verified on the live app: 0=checkbox, 1=Date, 2=EmployeeName,
    // 3=LeaveType, 4=LeaveBalance, 5=NumberOfDays, 6=Status, 7=Comments, 8=Actions
    protected final By leaveTypeColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(4)");
    protected final By statusColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(7)");

    protected LeaveListPageElements(WebDriver driver) {
        super(driver);
    }
}

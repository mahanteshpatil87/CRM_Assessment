package com.crmassessment.pages.leave;

import com.crmassessment.components.DropdownComponent;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.elements.leave.LeaveListPageElements;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class LeaveListPage extends LeaveListPageElements {

    private static final String ROUTE = "/web/index.php/leave/viewLeaveList";

    private final DropdownComponent dropdown;

    public LeaveListPage(WebDriver driver) {
        super(driver);
        this.dropdown = new DropdownComponent(driver);
    }

    public LeaveListPage open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
        return this;
    }

    public LeaveListPage filterByLeaveTypeAndStatus(String leaveType, String status) {
        dropdown.selectByLabel("Leave Type", leaveType);
        dropdown.selectByLabel("Show Leave with Status", status);
        click(searchButton);
        waitForFormLoaderToDisappear();
        return this;
    }

    public int getResultRowCount() {
        return countElements(tableRows);
    }

    public List<String> getLeaveTypeColumnValues() {
        return getTextsOf(leaveTypeColumn);
    }

    public List<String> getStatusColumnValues() {
        return getTextsOf(statusColumn);
    }

    // Deliberately no evidence-capture method here, unlike every other page
    // in the suite: this page's filtered result set is shared, ambient data
    // this test doesn't own, and can genuinely empty out between an
    // assertion and a screenshot attempt under real concurrent load on the
    // shared demo - a longer timeout doesn't fix that.
}

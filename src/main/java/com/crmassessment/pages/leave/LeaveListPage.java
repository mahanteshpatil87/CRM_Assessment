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

    public void open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
    }

    public void filterByLeaveTypeAndStatus(String leaveType, String status) {
        dropdown.selectByLabel("Leave Type", leaveType);
        dropdown.selectByLabel("Show Leave with Status", status);
        click(searchButton);
        waitForFormLoaderToDisappear();
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
}

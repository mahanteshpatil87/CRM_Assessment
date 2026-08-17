package com.crmassessment.tests.leave;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import org.testng.annotations.Test;

import java.util.List;

public class LeaveListTests extends AuthenticatedBaseTest {

    @Test(description = "TC-LVE-001: Filtering the Leave List by Leave Type and Status together returns only rows matching both")
    public void filterByLeaveTypeAndStatusShowsOnlyMatchingRows() {
        pages.leaveListPage
                .open();

        pages.leaveListPage
                .filterByLeaveTypeAndStatus("CAN - Personal", "Pending Approval");

        List<String> leaveTypes = pages.leaveListPage.getLeaveTypeColumnValues();
        List<String> statuses = pages.leaveListPage.getStatusColumnValues();

        boolean allLeaveTypesMatch = leaveTypes.stream().allMatch(type -> type.equals("CAN - Personal"));
        boolean allStatusesMatch = statuses.stream().allMatch(status -> status.contains("Pending Approval"));

        AssertsManager.getAsserts().assertTrue(
                allLeaveTypesMatch, "Every visible row's Leave Type should equal the selected filter value", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                allStatusesMatch, "Every visible row's Status should match the selected filter value", AssertionType.HARD);


    }
}

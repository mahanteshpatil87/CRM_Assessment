package com.crmassessment.pages;

import com.crmassessment.pages.admin.AddUserPage;
import com.crmassessment.pages.admin.AdminDashboardPage;
import com.crmassessment.pages.admin.AdminLoginPage;
import com.crmassessment.pages.admin.UserListPage;
import com.crmassessment.pages.leave.LeaveListPage;
import com.crmassessment.pages.pim.AddEmployeePage;
import com.crmassessment.pages.pim.EmployeeListPage;
import com.crmassessment.pages.pim.PersonalDetailsPage;
import com.crmassessment.pages.recruitment.AddCandidatePage;
import com.crmassessment.pages.recruitment.CandidateDetailsPage;
import com.crmassessment.pages.recruitment.CandidateListPage;
import org.openqa.selenium.WebDriver;

public class AllPages {

    public final AdminLoginPage adminLoginPage;
    public final AdminDashboardPage adminDashboardPage;
    public final AddUserPage addUserPage;
    public final UserListPage userListPage;

    public final EmployeeListPage employeeListPage;
    public final AddEmployeePage addEmployeePage;
    public final PersonalDetailsPage personalDetailsPage;

    public final LeaveListPage leaveListPage;

    public final AddCandidatePage addCandidatePage;
    public final CandidateListPage candidateListPage;
    public final CandidateDetailsPage candidateDetailsPage;

    public AllPages(WebDriver driver) {
        adminLoginPage = new AdminLoginPage(driver);
        adminDashboardPage = new AdminDashboardPage(driver);
        addUserPage = new AddUserPage(driver);
        userListPage = new UserListPage(driver);

        employeeListPage = new EmployeeListPage(driver);
        addEmployeePage = new AddEmployeePage(driver);
        personalDetailsPage = new PersonalDetailsPage(driver);

        leaveListPage = new LeaveListPage(driver);

        addCandidatePage = new AddCandidatePage(driver);
        candidateListPage = new CandidateListPage(driver);
        candidateDetailsPage = new CandidateDetailsPage(driver);
    }
}

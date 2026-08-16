package com.crmassessment.tests.admin;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.pages.admin.AddUserPage;
import com.crmassessment.pages.admin.UserListPage;
import com.crmassessment.pages.pim.AddEmployeePage;
import com.crmassessment.pages.pim.EmployeeListPage;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.util.List;

public class AdminUserTests extends AuthenticatedBaseTest {

    /**
     * A fresh employee (not yet linked to any login) so Add User's Employee
     * Name autocomplete has a guaranteed, unused target. The returned name
     * must stay unique (so the autocomplete match is unambiguous among the
     * shared demo's other employees) - carried on the last name only, so
     * the first name still reads as a real one.
     */
    private String[] createUnlinkedEmployee() {
        String[] name = TestDataUtils.randomNamePair();
        String firstName = name[0];
        String lastName = TestDataUtils.uniqueValue(name[1]);
        String employeeId = TestDataUtils.uniqueValue("QA");

        EmployeeListPage employeeListPage = new EmployeeListPage(DriverManager.getDriver());
        employeeListPage.open();
        AddEmployeePage addEmployeePage = employeeListPage.clickAdd();
        addEmployeePage.save(firstName, lastName, employeeId);
        return new String[]{firstName, lastName};
    }

    @Test(description = "TC-ADM-001: Adding a system user with a valid role, linked employee, and matching passwords succeeds")
    public void addUserWithValidDataSucceeds() {
        String[] employee = createUnlinkedEmployee();
        String employeeFullName = employee[0] + " " + employee[1];
        // Enterprise-style username convention: three letters of first name +
        // three letters of last name + a 3-digit number (e.g. "lucben482").
        String username = TestDataUtils.generateUsername(employee[0], employee[1]);
        String password = "AutoQa!Passw0rd1";

        UserListPage userListPage = new UserListPage(DriverManager.getDriver());
        userListPage.open();
        AddUserPage addUserPage = userListPage.clickAdd();

        addUserPage.addUser("ESS", employeeFullName, "Enabled", username, password);

        AssertsManager.getAsserts().assertTrue(
                addUserPage.isSavedSuccessfully(),
                "Add User should navigate to the System Users list on success", AssertionType.HARD);

        UserListPage listAfterCreate = new UserListPage(DriverManager.getDriver());
        listAfterCreate.open();
        listAfterCreate.searchByUsername(username);
        List<String> usernames = listAfterCreate.getUsernameColumnValues();

        AssertsManager.getAsserts().assertEquals(
                usernames.size(), 1,
                "Searching by the newly created unique username should return exactly one row", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                usernames.isEmpty() ? null : usernames.get(0), username,
                "The created user should appear in the System Users list with the exact username entered", AssertionType.HARD);
        listAfterCreate.captureUsernameEvidence();
    }

    @Test(description = "TC-ADM-002: Add User is blocked when Password and Confirm Password do not match")
    public void addUserWithMismatchedPasswordsIsBlocked() {
        UserListPage userListPage = new UserListPage(DriverManager.getDriver());
        userListPage.open();
        AddUserPage addUserPage = userListPage.clickAdd();

        // No employee is created/linked in this test, so the name pair here
        // is only a basis for a realistic username - same format as above.
        // generateUsername's own 3-digit suffix already makes the username
        // unique, so the name pair itself doesn't need a uniqueValue() wrap.
        String[] name = TestDataUtils.randomNamePair();
        addUserPage.enterUsername(TestDataUtils.generateUsername(name[0], name[1]));
        addUserPage.enterPassword("Passw0rd!Aa1");
        addUserPage.enterConfirmPassword("DifferentPass1!");
        addUserPage.clickSave();

        AssertsManager.getAsserts().assertEquals(
                addUserPage.getConfirmPasswordValidationMessage(), "Passwords do not match",
                "Confirm Password should show the exact mismatch validation message", AssertionType.HARD);
    }
}

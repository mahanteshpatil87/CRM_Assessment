package com.crmassessment.pages.admin;

import com.crmassessment.config.ConfigReader;
import com.crmassessment.elements.admin.UserListPageElements;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class UserListPage extends UserListPageElements {

    private static final String ROUTE = "/web/index.php/admin/viewSystemUsers";

    public UserListPage(WebDriver driver) {
        super(driver);
    }

    public UserListPage open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
        return this;
    }

    public UserListPage searchByUsername(String username) {
        type(usernameFilterInput, username);
        click(searchButton);
        // The form loader alone is not a reliable "results re-rendered"
        // signal for a simple list search (see EmployeeListPage for the
        // full explanation) - wait for the actual searched value to appear
        // in the results instead.
        waitForTextToBePresent(usernameColumn, username);
        return this;
    }

    public List<String> getUsernameColumnValues() {
        return getTextsOf(usernameColumn);
    }

    /** PASS-evidence for "the new user actually appears in the list" - attaches a screenshot with the username row outlined. */
    public UserListPage captureUsernameEvidence() {
        captureEvidence(usernameColumn, "Newly created username visible in System Users list");
        return this;
    }

    public AddUserPage clickAdd() {
        click(addButton);
        return new AddUserPage(driver);
    }
}

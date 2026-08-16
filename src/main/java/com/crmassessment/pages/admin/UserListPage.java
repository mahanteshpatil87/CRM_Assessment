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

    public void open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
    }

    public void searchByUsername(String username) {
        type(usernameFilterInput, username);
        click(searchButton);
        // The form loader alone is not a reliable "results re-rendered"
        // signal for a simple list search (see EmployeeListPage for the
        // full explanation) - wait for the actual searched value to appear
        // in the results instead.
        waitForTextToBePresent(usernameColumn, username);
    }

    public List<String> getUsernameColumnValues() {
        return getTextsOf(usernameColumn);
    }

    public AddUserPage clickAdd() {
        click(addButton);
        return new AddUserPage(driver);
    }
}

package com.crmassessment.pages.admin;

import com.crmassessment.components.AutocompleteComponent;
import com.crmassessment.components.DropdownComponent;
import com.crmassessment.elements.admin.AddUserPageElements;
import org.openqa.selenium.WebDriver;

public class AddUserPage extends AddUserPageElements {

    private final DropdownComponent dropdown;
    private final AutocompleteComponent autocomplete;

    public AddUserPage(WebDriver driver) {
        super(driver);
        this.dropdown = new DropdownComponent(driver);
        this.autocomplete = new AutocompleteComponent(driver);
    }

    public void selectUserRole(String role) {
        dropdown.selectByLabel("User Role", role);
    }

    public void selectEmployee(String employeeNameSearchText) {
        autocomplete.selectFirstSuggestion("Employee Name", employeeNameSearchText);
    }

    public void selectStatus(String status) {
        dropdown.selectByLabel("Status", status);
    }

    public void enterUsername(String username) {
        type(usernameInput, username);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void enterConfirmPassword(String password) {
        type(confirmPasswordInput, password);
    }

    public void clickSave() {
        click(saveButton);
    }

    public void addUser(String role, String employeeNameSearchText, String status, String username, String password) {
        selectUserRole(role);
        selectEmployee(employeeNameSearchText);
        selectStatus(status);
        enterUsername(username);
        enterPassword(password);
        enterConfirmPassword(password);
        clickSave();
    }

    /**
     * Waits for the confirmed real post-save redirect (verified against the
     * live app: a successful Add User save navigates to
     * /admin/viewSystemUsers) before returning, so callers that immediately
     * navigate elsewhere (e.g. straight to a follow-up search) don't race
     * the still-in-flight save request.
     */
    public boolean isSavedSuccessfully() {
        return waitForUrlMatches(".*/viewSystemUsers.*");
    }

    public int getValidationMessageCount() {
        isDisplayed(validationMessages); // wait for the first message to render before counting - avoids a race right after clickSave()
        return countElements(validationMessages);
    }

    public String getConfirmPasswordValidationMessage() {
        return getText(confirmPasswordValidationMessage);
    }
}

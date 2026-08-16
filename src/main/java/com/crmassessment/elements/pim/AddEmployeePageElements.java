package com.crmassessment.elements.pim;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Add Employee page, and nothing else. */
public class AddEmployeePageElements extends BasePage {

    protected final By firstNameInput = By.cssSelector("input[placeholder='First Name']");
    protected final By lastNameInput = By.cssSelector("input[placeholder='Last Name']");
    protected final By employeeIdInput = inputForLabel("Employee Id");
    protected final By saveButton = By.cssSelector("button[type='submit']");
    protected final By validationMessages = By.cssSelector(".oxd-input-group__message");

    protected AddEmployeePageElements(WebDriver driver) {
        super(driver);
    }
}

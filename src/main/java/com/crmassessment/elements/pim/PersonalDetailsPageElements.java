package com.crmassessment.elements.pim;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Personal Details page, and nothing else. */
public class PersonalDetailsPageElements extends BasePage {

    protected final By employeeFullNameHeading = By.cssSelector(".orangehrm-edit-employee-name");

    protected PersonalDetailsPageElements(WebDriver driver) {
        super(driver);
    }
}

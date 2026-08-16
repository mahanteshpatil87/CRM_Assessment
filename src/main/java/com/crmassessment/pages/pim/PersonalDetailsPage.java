package com.crmassessment.pages.pim;

import com.crmassessment.elements.pim.PersonalDetailsPageElements;
import org.openqa.selenium.WebDriver;

public class PersonalDetailsPage extends PersonalDetailsPageElements {

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    public String getEmployeeFullName() {
        waitForNonEmptyText(employeeFullNameHeading);
        return getText(employeeFullNameHeading);
    }

    public boolean isDisplayed() {
        // A same-SPA navigation (Save -> Personal Details) takes a moment to
        // actually update the URL - checking getCurrentUrl() synchronously
        // right after clickSave() returns can read the still-old URL and
        // report false before the navigation has even happened (verified:
        // getEmployeeFullName(), which doesn't URL-check first and instead
        // just keeps polling for the element/text to appear, was unaffected
        // by this same race - isDisplayed() needs the equivalent wait).
        if (!waitForUrlMatches(".*/viewPersonalDetails.*")) {
            return false;
        }
        try {
            waitForNonEmptyText(employeeFullNameHeading);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}

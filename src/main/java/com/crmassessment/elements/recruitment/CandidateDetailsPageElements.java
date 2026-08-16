package com.crmassessment.elements.recruitment;

import com.crmassessment.pages.base.BasePage;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Candidate Details page, and nothing else. */
public class CandidateDetailsPageElements extends BasePage {

    // Verified structure: label "Resume" -> .oxd-input-group -> p.orangehrm-file-name
    // (filename text, also in its title attribute) containing i.orangehrm-file-download.
    protected final By resumeFileName = By.xpath(String.format(
            "//label[normalize-space()='Resume']/ancestor::div[%s][1]//p[contains(@class,'orangehrm-file-name')]",
            LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    protected final By resumeDownloadIcon = By.xpath(String.format(
            "//label[normalize-space()='Resume']/ancestor::div[%s][1]//i[contains(@class,'orangehrm-file-download')]",
            LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));

    protected CandidateDetailsPageElements(WebDriver driver) {
        super(driver);
    }
}

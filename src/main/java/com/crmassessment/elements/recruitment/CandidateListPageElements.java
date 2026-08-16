package com.crmassessment.elements.recruitment;

import com.crmassessment.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Every locator used on the Candidate List page, and nothing else. */
public class CandidateListPageElements extends BasePage {

    protected final By searchButton = By.cssSelector("button[type='submit']");
    protected final By addButton = By.xpath("//button[normalize-space()='Add']");
    // Cell order verified on the live app: 0=checkbox, 1=Vacancy, 2=Candidate,
    // 3=HiringManager, 4=DateOfApplication, 5=Status, 6=Actions
    protected final By candidateNameColumn = By.cssSelector(".oxd-table-body .oxd-table-row .oxd-table-cell:nth-child(3)");
    // Action icons have no consistent position across OrangeHRM tables (verified) -
    // always resolve "view" by its bi-eye-fill icon class, never by button index.
    protected final By firstViewIcon = By.xpath("(//div[contains(@class,'oxd-table-body')]//i[contains(@class,'bi-eye-fill')])[1]");

    protected CandidateListPageElements(WebDriver driver) {
        super(driver);
    }
}

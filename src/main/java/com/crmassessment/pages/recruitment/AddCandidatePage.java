package com.crmassessment.pages.recruitment;

import com.crmassessment.components.DropdownComponent;
import com.crmassessment.components.FileUploadComponent;
import com.crmassessment.elements.recruitment.AddCandidatePageElements;
import org.openqa.selenium.WebDriver;

public class AddCandidatePage extends AddCandidatePageElements {

    private final DropdownComponent dropdown;
    private final FileUploadComponent fileUpload;

    public AddCandidatePage(WebDriver driver) {
        super(driver);
        this.dropdown = new DropdownComponent(driver);
        this.fileUpload = new FileUploadComponent(driver);
    }

    public void enterFirstName(String firstName) {
        type(firstNameInput, firstName);
    }

    public void enterLastName(String lastName) {
        type(lastNameInput, lastName);
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterContactNumber(String contactNumber) {
        type(contactNumberInput, contactNumber);
    }

    public void selectVacancy(String vacancyName) {
        dropdown.selectByLabel("Vacancy", vacancyName);
    }

    public void uploadResume(String absoluteFilePath) {
        fileUpload.uploadByLabel("Resume", absoluteFilePath);
    }

    public void checkConsent() {
        setCheckbox(consentCheckbox, true);
    }

    public void clickSave() {
        click(saveButton);
    }

    /** Mandatory fields verified on the live app: First Name, Last Name, Email. */
    public void addCandidateMinimal(String firstName, String lastName, String email) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmail(email);
        clickSave();
    }

    public void addCandidateFull(String firstName, String lastName, String email, String contactNumber,
                                  String vacancyName, String resumeAbsolutePath) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmail(email);
        enterContactNumber(contactNumber);
        selectVacancy(vacancyName);
        uploadResume(resumeAbsolutePath);
        checkConsent();
        clickSave();
    }

    public int getValidationMessageCount() {
        isDisplayed(validationMessages); // wait for the first message to render before counting - avoids a race right after clickSave()
        return countElements(validationMessages);
    }

    /**
     * Waits for either outcome of Save: a successful save navigates the SPA
     * route to a numeric candidate id (e.g. .../addCandidate/83); a rejected
     * save (validation error) leaves the URL unchanged, so this correctly
     * returns false once the wait times out rather than checking too early.
     */
    public boolean isSavedSuccessfully() {
        return waitForUrlMatches(".*/addCandidate/\\d+$");
    }
}

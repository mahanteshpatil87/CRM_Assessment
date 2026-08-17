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

    public AddCandidatePage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        return this;
    }

    public AddCandidatePage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        return this;
    }

    public AddCandidatePage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public AddCandidatePage enterContactNumber(String contactNumber) {
        type(contactNumberInput, contactNumber);
        return this;
    }

    public AddCandidatePage selectVacancy(String vacancyName) {
        dropdown.selectByLabel("Vacancy", vacancyName);
        return this;
    }

    public AddCandidatePage uploadResume(String absoluteFilePath) {
        fileUpload.uploadByLabel("Resume", absoluteFilePath);
        return this;
    }

    public AddCandidatePage checkConsent() {
        setCheckbox(consentCheckbox, true);
        return this;
    }

    public AddCandidatePage clickSave() {
        click(saveButton);
        return this;
    }

    /**
     * Fills every field a candidate-with-resume test needs and saves in one
     * call: mandatory fields (First Name, Last Name, Email, verified on the
     * live app) plus the resume upload and consent checkbox. Covers every
     * "add a candidate with a resume" test in the suite, whether the save
     * is expected to succeed or be rejected (e.g. an invalid file type) -
     * checking consent doesn't affect that outcome either way.
     */
    public AddCandidatePage addCandidateWithResume(String firstName, String lastName, String email, String resumeAbsolutePath) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterEmail(email);
        uploadResume(resumeAbsolutePath);
        checkConsent();
        clickSave();
        return this;
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

    /** PASS-evidence for "the candidate was actually saved" - attaches a screenshot with the (now-saved) first name field outlined. */
    public AddCandidatePage captureSavedCandidateEvidence() {
        waitForNonEmptyValue(firstNameInput);
        captureEvidence(firstNameInput, "Saved candidate's name visible after Add Candidate succeeds");
        return this;
    }

    /** PASS-evidence for "the rejected save left us on the same form" - attaches a screenshot with the Save button still present. */
    public AddCandidatePage captureStillOnFormEvidence() {
        captureEvidence(saveButton, "Add Candidate form still shown - the rejected save did not navigate away");
        return this;
    }
}

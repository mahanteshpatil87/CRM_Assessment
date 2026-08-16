package com.crmassessment.tests.recruitment;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.pages.recruitment.AddCandidatePage;
import com.crmassessment.pages.recruitment.CandidateListPage;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.io.File;

public class RecruitmentTests extends AuthenticatedBaseTest {

    @Test(description = "TC-REC-001: Adding a candidate with a valid resume upload succeeds")
    public void addCandidateWithValidResumeSucceeds() {
        String firstName = TestDataUtils.uniqueValue("AutoQaCand");
        String lastName = "TestUser";
        String email = TestDataUtils.uniqueEmail("autoqacand");
        String resumePath = new File(ConfigReader.getValidResumeFilePath()).getAbsolutePath();

        CandidateListPage candidateListPage = new CandidateListPage(DriverManager.getDriver());
        candidateListPage.open();
        AddCandidatePage addCandidatePage = candidateListPage.clickAdd();

        addCandidatePage.enterFirstName(firstName);
        addCandidatePage.enterLastName(lastName);
        addCandidatePage.enterEmail(email);
        addCandidatePage.uploadResume(resumePath);
        addCandidatePage.checkConsent();
        addCandidatePage.clickSave();

        AssertsManager.getAsserts().assertTrue(
                addCandidatePage.isSavedSuccessfully(),
                "Saving a candidate with mandatory fields and a valid resume should succeed and assign a candidate id", AssertionType.HARD);
    }

    @Test(description = "TC-REC-002: Uploading a resume file outside the accepted types is rejected")
    public void addCandidateWithInvalidResumeTypeIsRejected() {
        String firstName = TestDataUtils.uniqueValue("AutoQaCandBad");
        String lastName = "TestUser";
        String email = TestDataUtils.uniqueEmail("autoqacandbad");
        String invalidResumePath = new File(ConfigReader.getInvalidResumeFilePath()).getAbsolutePath();

        CandidateListPage candidateListPage = new CandidateListPage(DriverManager.getDriver());
        candidateListPage.open();
        AddCandidatePage addCandidatePage = candidateListPage.clickAdd();

        addCandidatePage.enterFirstName(firstName);
        addCandidatePage.enterLastName(lastName);
        addCandidatePage.enterEmail(email);
        addCandidatePage.uploadResume(invalidResumePath);
        addCandidatePage.clickSave();

        AssertsManager.getAsserts().assertFalse(
                addCandidatePage.isSavedSuccessfully(),
                "A candidate save with a resume file outside the accepted types/size must not succeed", AssertionType.HARD);
    }
}

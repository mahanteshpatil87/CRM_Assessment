package com.crmassessment.tests.recruitment;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.io.File;

public class RecruitmentTests extends AuthenticatedBaseTest {

    @Test(description = "TC-REC-001: Adding a candidate with a valid resume upload succeeds")
    public void addCandidateWithValidResumeSucceeds() {
        // No search is involved in this test (only a URL-based save check),
        // so unlike CandidateResumeEndToEndTests, the name never needs to be
        // unique - a plain real-looking name is enough.
        String[] name = TestDataUtils.randomNamePair();
        String firstName = name[0];
        String lastName = name[1];
        String email = TestDataUtils.uniqueEmail("autoqacand");
        String resumePath = new File(ConfigReader.getValidResumeFilePath()).getAbsolutePath();

        pages.candidateListPage
                .open();

        pages.candidateListPage
                .clickAdd();

        pages.addCandidatePage
                .addCandidateWithResume(firstName, lastName, email, resumePath);

        AssertsManager.getAsserts().assertTrue(
                pages.addCandidatePage.isSavedSuccessfully(),
                "Saving a candidate with mandatory fields and a valid resume should succeed and assign a candidate id", AssertionType.HARD);

        pages.addCandidatePage
                .captureSavedCandidateEvidence();
    }

    @Test(description = "TC-REC-002: Uploading a resume file outside the accepted types is rejected")
    public void addCandidateWithInvalidResumeTypeIsRejected() {
        String[] name = TestDataUtils.randomNamePair();
        String firstName = name[0];
        String lastName = name[1];
        String email = TestDataUtils.uniqueEmail("autoqacandbad");
        String invalidResumePath = new File(ConfigReader.getInvalidResumeFilePath()).getAbsolutePath();

        pages.candidateListPage
                .open();

        pages.candidateListPage
                .clickAdd();

        pages.addCandidatePage
                .addCandidateWithResume(firstName, lastName, email, invalidResumePath);

        AssertsManager.getAsserts().assertFalse(
                pages.addCandidatePage.isSavedSuccessfully(),
                "A candidate save with a resume file outside the accepted types/size must not succeed", AssertionType.HARD);

        pages.addCandidatePage
                .captureStillOnFormEvidence();
    }
}

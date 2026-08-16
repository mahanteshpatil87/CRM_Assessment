package com.crmassessment.tests.endtoend;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.AuthenticatedBaseTest;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.pages.recruitment.AddCandidatePage;
import com.crmassessment.pages.recruitment.CandidateDetailsPage;
import com.crmassessment.pages.recruitment.CandidateListPage;
import com.crmassessment.utils.FileUtils;
import com.crmassessment.utils.TestDataUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class CandidateResumeEndToEndTests extends AuthenticatedBaseTest {

    @Test(description = "TC-REC-005: End-to-end - create a candidate with an uploaded resume, find them by search, "
            + "and confirm the resume can be downloaded intact from their detail page")
    public void candidateResumeCanBeUploadedFoundAndDownloaded() {
        String firstName = TestDataUtils.uniqueValue("AutoQaE2E");
        String lastName = "TestUser";
        String email = TestDataUtils.uniqueEmail("autoqae2e");
        File originalResume = new File(ConfigReader.getValidResumeFilePath());
        FileUtils.clearDirectory(ConfigReader.getDownloadDir());

        // 1. Create the candidate with an uploaded resume
        CandidateListPage candidateListPage = new CandidateListPage(DriverManager.getDriver());
        candidateListPage.open();
        AddCandidatePage addCandidatePage = candidateListPage.clickAdd();
        addCandidatePage.enterFirstName(firstName);
        addCandidatePage.enterLastName(lastName);
        addCandidatePage.enterEmail(email);
        addCandidatePage.uploadResume(originalResume.getAbsolutePath());
        addCandidatePage.checkConsent();
        addCandidatePage.clickSave();

        AssertsManager.getAsserts().assertTrue(
                addCandidatePage.isSavedSuccessfully(),
                "Candidate creation with a resume upload should succeed before continuing the workflow", AssertionType.HARD);

        // 2. Find the candidate via search
        CandidateListPage listAfterCreate = new CandidateListPage(DriverManager.getDriver());
        listAfterCreate.open();
        listAfterCreate.searchByCandidateName(firstName);

        AssertsManager.getAsserts().assertEquals(
                listAfterCreate.getResultRowCount(), 1,
                "Searching by the newly created candidate's unique name should return exactly one row", AssertionType.HARD);

        // 3. Open their detail page and confirm the resume attachment is present
        CandidateDetailsPage detailsPage = listAfterCreate.openFirstResult();
        AssertsManager.getAsserts().assertTrue(
                detailsPage.hasResumeAttachment(),
                "The candidate's detail page should show the previously uploaded resume attachment", AssertionType.HARD);

        // 4. Download it and verify the downloaded file matches the original
        detailsPage.downloadResume();
        Optional<Path> downloaded = FileUtils.waitForDownloadedFile(
                ConfigReader.getDownloadDir(), originalResume.getName(), 15);

        AssertsManager.getAsserts().assertTrue(
                downloaded.isPresent(),
                "The resume download should complete and land in the configured download directory", AssertionType.HARD);

        downloaded.ifPresent(path -> AssertsManager.getAsserts().assertTrue(
                FileUtils.haveSameContent(originalResume.toPath(), path),
                "The downloaded resume's content should be byte-identical to the file originally uploaded", AssertionType.HARD));
    }
}

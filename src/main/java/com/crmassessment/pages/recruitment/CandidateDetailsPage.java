package com.crmassessment.pages.recruitment;

import com.crmassessment.elements.recruitment.CandidateDetailsPageElements;
import org.openqa.selenium.WebDriver;

public class CandidateDetailsPage extends CandidateDetailsPageElements {

    public CandidateDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasResumeAttachment() {
        return isDisplayed(resumeFileName);
    }

    public String getResumeFileName() {
        return getAttribute(resumeFileName, "title");
    }

    /** PASS-evidence for "the uploaded resume attachment is actually present" - attaches a screenshot with the filename outlined. */
    public CandidateDetailsPage captureResumeAttachmentEvidence() {
        waitForNonEmptyText(resumeFileName);
        captureEvidence(resumeFileName, "Uploaded resume attachment visible on candidate's detail page");
        return this;
    }

    public CandidateDetailsPage downloadResume() {
        click(resumeDownloadIcon);
        return this;
    }
}

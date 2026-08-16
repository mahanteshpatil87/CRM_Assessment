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

    public void downloadResume() {
        click(resumeDownloadIcon);
    }
}

package com.crmassessment.components;

import com.crmassessment.utils.ElementActions;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Drives file upload fields identified by their visible label (the same
 * label-anchored strategy as DropdownComponent - these hidden
 * &lt;input type="file"&gt; elements carry no id/name either).
 */
public class FileUploadComponent {

    private final ElementActions elementActions;

    public FileUploadComponent(WebDriver driver) {
        this.elementActions = new ElementActions(driver);
    }

    public void uploadByLabel(String fieldLabel, String absoluteFilePath) {
        By fileInput = By.xpath(String.format(
                "//label[normalize-space()='%s']/ancestor::div[%s][1]" +
                        "//input[@type='file']",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
        elementActions.uploadFile(fileInput, absoluteFilePath);
    }
}

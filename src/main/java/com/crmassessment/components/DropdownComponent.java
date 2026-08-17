package com.crmassessment.components;

import com.crmassessment.utils.ElementActions;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Drives OrangeHRM's custom dropdown widget (div-based oxd-select-* markup,
 * not a native &lt;select&gt;), so Selenium's Select class cannot be used.
 * None of these elements carry id/name/aria-label, so every field is
 * resolved by its visible label text - the only stable anchor available -
 * scoped to that label's containing .oxd-input-group so fields with
 * duplicate-looking structure never collide.
 */
public class DropdownComponent {

    private final ElementActions elementActions;

    public DropdownComponent(WebDriver driver) {
        this.elementActions = new ElementActions(driver);
    }

    public void selectByLabel(String fieldLabel, String optionText) {
        elementActions.click(triggerFor(fieldLabel));
        elementActions.click(optionMatching(optionText));
    }

    /** The currently selected option's visible text, for asserting a selection was saved/persisted. */
    public String getSelectedText(String fieldLabel) {
        return elementActions.getText(triggerFor(fieldLabel));
    }

    private By triggerFor(String fieldLabel) {
        return By.xpath(String.format(
                "//label[normalize-space()='%s']/ancestor::div[%s][1]" +
                        "//div[contains(@class,'oxd-select-text')]",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    }

    private By optionMatching(String optionText) {
        return By.xpath(String.format(
                "//div[contains(@class,'oxd-select-option')][normalize-space()='%s']",
                optionText));
    }
}

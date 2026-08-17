package com.crmassessment.components;

import com.crmassessment.utils.ElementActions;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Drives OrangeHRM's autocomplete text fields (Employee Name, Candidate
 * Name, etc.): typing renders a suggestion list, and a value is only
 * captured by the form once a suggestion is actually clicked - typing text
 * that matches nothing leaves the field logically empty even though text
 * is visibly present. Reused across PIM, Admin, and Recruitment page
 * objects, so it lives here rather than being duplicated per page.
 */
public class AutocompleteComponent {

    private final ElementActions elementActions;

    public AutocompleteComponent(WebDriver driver) {
        this.elementActions = new ElementActions(driver);
    }

    /**
     * Types into the field for the given label and clicks the first
     * matching suggestion, which is what actually populates the field's
     * bound value for the form to submit.
     * <p>
     * The suggestion locator is scoped to the same field's .oxd-input-group
     * (the dropdown renders nested inside it, not portaled elsewhere)
     * rather than searching the whole document for "any autocomplete
     * dropdown, first match" - a page can have more than one autocomplete
     * field (e.g. Employee List's Employee Name *and* Supervisor Name), so
     * an unscoped locator risks resolving to the wrong field's suggestions.
     * <p>
     * Also waits for the rendered suggestion to actually contain the typed
     * text before clicking. The suggestion list is debounced per keystroke,
     * and Selenium's sendKeys() types a long generated name far faster than
     * a human would - without this wait, the click can land on a stale
     * suggestion left over from an earlier, partially-typed substring
     * instead of the final query's real result.
     */
    public void selectFirstSuggestion(String fieldLabel, String searchText) {
        type(fieldLabel, searchText);
        By suggestion = firstSuggestionFor(fieldLabel);
        elementActions.waitForTextToBePresent(suggestion, searchText);
        elementActions.click(suggestion);
    }

    private By firstSuggestionFor(String fieldLabel) {
        return By.xpath(String.format(
                "(//label[normalize-space()='%s']/ancestor::div[%s][1]" +
                        "//div[contains(@class,'oxd-autocomplete-option')])[1]",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    }

    /** Types into the field without selecting a suggestion - used to inspect the suggestion list itself (e.g. a "no matches" case). */
    public void type(String fieldLabel, String searchText) {
        elementActions.type(inputFor(fieldLabel), searchText);
    }

    /** True when the open suggestion list is showing "No Records Found" for the current input. */
    public boolean isShowingNoRecordsFound() {
        By noRecords = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-dropdown')][contains(normalize-space(),'No Records Found')]");
        return elementActions.isDisplayed(noRecords);
    }

    private By inputFor(String fieldLabel) {
        return By.xpath(String.format(
                "//label[normalize-space()='%s']/ancestor::div[%s][1]//input",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    }
}

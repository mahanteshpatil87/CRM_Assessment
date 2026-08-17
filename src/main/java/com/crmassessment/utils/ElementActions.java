package com.crmassessment.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * The single source of truth for low-level Selenium interactions.
 * Every action here is wait-wrapped via WaitUtils. BasePage and the
 * components/* classes hold an instance of this and delegate to it -
 * no other class should call raw WebElement methods directly.
 */
public class ElementActions {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public void click(By locator) {
        waitUtils.waitForClickability(locator).click();
    }

    public void type(By locator, String text) {
        WebElement element = waitUtils.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public void doubleClick(By locator) {
        WebElement element = waitUtils.waitForClickability(locator);
        new Actions(driver).doubleClick(element).perform();
    }

    public void rightClick(By locator) {
        WebElement element = waitUtils.waitForClickability(locator);
        new Actions(driver).contextClick(element).perform();
    }

    public void hover(By locator) {
        WebElement element = waitUtils.waitForVisibility(locator);
        new Actions(driver).moveToElement(element).perform();
    }

    public void scrollToElement(By locator) {
        WebElement element = waitUtils.waitForPresence(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public String getText(By locator) {
        return waitUtils.waitForVisibility(locator).getText();
    }

    public String getAttribute(By locator, String attributeName) {
        return waitUtils.waitForPresence(locator).getAttribute(attributeName);
    }

    /**
     * Captures full-page PASS-evidence for a specific validated element,
     * outlined so it's visually obvious what's being proven (e.g. a newly
     * created employee's name), and attaches it to the current test's
     * ExtentReports entry. See ScreenshotUtils.captureHighlightedAndAttach
     * for why this exists separately from the failure-only screenshot path.
     */
    public void captureEvidence(By locator, String description) {
        WebElement element = waitUtils.waitForVisibility(locator);
        ScreenshotUtils.captureHighlightedAndAttach(driver, element, description);
    }


    /** Same as captureEvidence, but for the Nth match of a repeated locator (e.g. one specific row's checkbox). */
    public void captureNthEvidence(By locator, int index, String description) {
        WebElement element = waitUtils.waitForNthVisible(locator, index);
        ScreenshotUtils.captureHighlightedAndAttach(driver, element, description);
    }

    public boolean isDisplayed(By locator) {
        try {
            return waitUtils.waitForVisibility(locator).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** Reading .isSelected() is a plain DOM property read - doesn't require the element to be visually displayed (see setCheckbox's opacity:0 checkbox note). */
    public boolean isSelected(By locator) {
        return waitUtils.waitForPresence(locator).isSelected();
    }

    public boolean isEnabled(By locator) {
        return waitUtils.waitForVisibility(locator).isEnabled();
    }

    /**
     * Sets a checkbox to a specific desired state, rather than blindly clicking.
     * Blindly clicking would toggle the wrong way if the checkbox's current
     * state doesn't match what the test assumes - this checks first.
     * <p>
     * OrangeHRM's checkboxes (Consent to keep data, Employee List row
     * selects, etc.) render the real &lt;input type="checkbox"&gt; with
     * opacity:0 and show a styled sibling &lt;span&gt; instead - verified
     * against the live DOM. Selenium correctly refuses to click an
     * invisible element, so the state is read from the input but the
     * click is dispatched to its enclosing, visible &lt;label&gt;, which
     * natively toggles the input per standard HTML label/input association.
     */
    public void setCheckbox(By locator, boolean shouldBeChecked) {
        WebElement checkbox = waitUtils.waitForPresence(locator);
        if (checkbox.isSelected() != shouldBeChecked) {
            checkbox.findElement(By.xpath("./ancestor::label[1]")).click();
        }
    }

    // ---- Standard HTML <select> dropdown handling ----

    public void selectDropdownByVisibleText(By locator, String visibleText) {
        WebElement dropdown = waitUtils.waitForVisibility(locator);
        new Select(dropdown).selectByVisibleText(visibleText);
    }

    public void selectDropdownByValue(By locator, String value) {
        WebElement dropdown = waitUtils.waitForVisibility(locator);
        new Select(dropdown).selectByValue(value);
    }

    public String getSelectedDropdownText(By locator) {
        WebElement dropdown = waitUtils.waitForVisibility(locator);
        return new Select(dropdown).getFirstSelectedOption().getText();
    }

    // ---- File upload ----

    /**
     * Uploads a file via a hidden <input type="file">. Selenium's sendKeys
     * on a file input requires a real absolute filesystem path - this is
     * why upload assets live in test-data/files/upload/ (project root),
     * not src/resources (see earlier discussion on classpath vs filesystem paths).
     */
    public void uploadFile(By locator, String absoluteFilePath) {
        WebElement fileInput = waitUtils.waitForPresence(locator);
        fileInput.sendKeys(absoluteFilePath);
    }

    // ---- Multi-element helpers ----

    public List<WebElement> findAllVisible(By locator) {
        return waitUtils.waitForAllVisible(locator);
    }

    public int countElements(By locator) {
        return driver.findElements(locator).size();
    }

    /**
     * Clicks the Nth match of a repeated locator (e.g. the 3rd row checkbox
     * in a results table). Used by BulkCheckboxComponent, which has no
     * per-row id/name to target individually - only DOM order.
     */
    public void clickNth(By locator, int index) {
        waitUtils.waitForNthVisible(locator, index).click();
    }

    public boolean isNthSelected(By locator, int index) {
        return waitUtils.waitForAllPresence(locator).get(index).isSelected();
    }

    public boolean waitForNthSelected(By locator, int index, boolean expectedSelected) {
        return waitUtils.waitForNthSelected(locator, index, expectedSelected);
    }

    public boolean waitForNthSelected(By locator, int index, boolean expectedSelected, java.time.Duration timeout) {
        return waitUtils.waitForNthSelected(locator, index, expectedSelected, timeout);
    }

    public boolean waitForUrlMatches(String regex) {
        return waitUtils.waitForUrlMatches(regex);
    }

    /**
     * Waits for an async-auto-populated field's default value to actually
     * land, then replaces it. Uses a real select-all + delete keystroke
     * sequence rather than WebElement.clear(): verified against the live
     * app that .clear() does not reliably clear this Vue-controlled input -
     * it left the DOM value showing the old default value with the newly
     * typed text appended after it (e.g. "0438" + "QA71803352" instead of
     * just "QA71803352"), because Vue's v-model didn't register clear()'s
     * DOM-level change as a real input event the way it does real keystrokes.
     */
    public void typeOverAutoPopulatedValue(By locator, String text) {
        waitUtils.waitForNonEmptyValue(locator);
        WebElement element = waitUtils.waitForVisibility(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
    }

    public void waitForFormLoaderToDisappear() {
        waitUtils.waitForFormLoaderToDisappear();
    }

    public void waitForNonEmptyValue(By locator) {
        waitUtils.waitForNonEmptyValue(locator);
    }

    public void waitForNonEmptyText(By locator) {
        waitUtils.waitForNonEmptyText(locator);
    }

    public void waitForTextToBePresent(By locator, String text) {
        waitUtils.waitForTextToBePresent(locator, text);
    }

    /**
     * Text of every match for a repeated locator, in DOM order - e.g. every
     * value in a single results-table column, for asserting a filter
     * actually narrowed every row rather than just the first one.
     * <p>
     * Tolerates zero matches as a legitimate outcome (e.g. a filter that
     * genuinely matches no rows on a live, constantly-changing shared demo)
     * rather than treating it as a failure to wait out - the caller's own
     * "every row matches X" assertion is vacuously true over an empty list,
     * so failing here would reject a valid result for the wrong reason.
     * <p>
     * Retries on a stale element rather than letting it propagate: the
     * element list is fetched once, then read one at a time, and a results
     * table can re-render between that fetch and a later element's read
     * (verified live: this raced against Employee List's status-filter
     * results). A staleness mid-read means the whole snapshot is now
     * meaningless, not just the one element - re-fetching the full list
     * fresh and starting over is the only safe recovery.
     */
    public List<String> getTextsOf(By locator) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                List<String> texts = new java.util.ArrayList<>();
                for (WebElement element : waitUtils.waitForAllVisible(locator)) {
                    texts.add(element.getText());
                }
                return texts;
            } catch (org.openqa.selenium.TimeoutException e) {
                // no matches ever appeared - genuinely zero rows, not a loading race
                return new java.util.ArrayList<>();
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                // fall through and retry with a fresh fetch
            }
        }
        throw new org.openqa.selenium.StaleElementReferenceException(
                "Elements for " + locator + " kept going stale across retries");
    }
}
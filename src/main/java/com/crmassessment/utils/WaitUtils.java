package com.crmassessment.utils;

import com.crmassessment.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Owns every explicit-wait condition used in the framework.
 * ElementActions and the components/* classes call into this rather than
 * each creating their own WebDriverWait - keeps wait strategy in one place.
 */
public class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getExplicitTimeoutSeconds()));
    }

    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    public WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits for OrangeHRM's form-level loading overlay (.oxd-form-loader) to
     * be gone. Returns immediately (true) if it was never shown in the first
     * place - invisibilityOfElementLocated treats "not present" as success.
     * Verified against the live app: typing into an async-validated field
     * (e.g. Add Employee's Employee Id, which triggers a server-side
     * uniqueness check) can leave this overlay up long enough that an
     * immediately-following Save click gets ElementClickInterceptedException
     * with the loader reported as the element actually receiving the click.
     */
    public boolean waitForFormLoaderToDisappear() {
        return waitForInvisibility(By.cssSelector(".oxd-form-loader"));
    }

    public List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits until the Nth match of a repeated locator is visible, and
     * returns it - unlike waitForAllVisible, this doesn't require every
     * match in the collection to be simultaneously visible, only the one
     * actually needed. Requiring the whole collection is needlessly
     * fragile when the match count can shift for reasons unrelated to the
     * specific element being acted on (verified live: clicking the Nth row
     * checkbox in a results table timed out because the table's total row
     * count wasn't staying constant on the shared, concurrently-used
     * public demo, even though the specific row being clicked was fine).
     */
    public WebElement waitForNthVisible(By locator, int index) {
        return wait.until(driver -> {
            try {
                List<WebElement> elements = driver.findElements(locator);
                if (index >= elements.size()) {
                    return null;
                }
                WebElement element = elements.get(index);
                return element.isDisplayed() ? element : null;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                return null;
            }
        });
    }

    public List<WebElement> waitForAllPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForTextToBePresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /** Polls the URL against a regex, returning false (not throwing) on timeout - used to assert a save either did or did not navigate. */
    public boolean waitForUrlMatches(String regex) {
        try {
            return wait.until(ExpectedConditions.urlMatches(regex));
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits until a field's value attribute is non-empty - for fields the
     * app auto-populates asynchronously after the page itself has already
     * rendered (e.g. Add Employee's suggested next Employee Id). Typing
     * into such a field before that populate lands races it: clear() wipes
     * an already-empty field, sendKeys() lands first, and the late default
     * then gets merged into what was just typed instead of replaced.
     * Verified against the live app: entering "QA70150671" without this
     * wait produced a corrupted field value of "0416QA70150671".
     */
    public boolean waitForNonEmptyValue(By locator) {
        WebElement element = waitForVisibility(locator);
        return wait.until(ExpectedConditions.attributeToBeNotEmpty(element, "value"));
    }

    /**
     * Waits until the Nth match of a repeated locator's isSelected() equals
     * the expected state - for actions where clicking one control (e.g. a
     * bulk "select all" header checkbox) triggers an async state change on
     * OTHER elements (each row's own checkbox) rather than the clicked
     * element itself. Verified live: reading a row's checked state
     * immediately after clicking "select all" can race OrangeHRM's Vue app
     * propagating that state down to the row - the header click completes
     * and returns before every row checkbox has actually re-rendered as
     * checked.
     */
    public boolean waitForNthSelected(By locator, int index, boolean expectedSelected) {
        return waitForNthSelected(locator, index, expectedSelected,
                Duration.ofSeconds(ConfigReader.getExplicitTimeoutSeconds()));
    }

    /**
     * Same as {@link #waitForNthSelected(By, int, boolean)} but with a
     * caller-supplied timeout, for the rare case where the default explicit
     * timeout genuinely isn't enough margin - verified live: propagating a
     * bulk "select all" down to every row checkbox occasionally took longer
     * than the default 30s under real load on the shared demo, not just a
     * brief client-side race.
     */
    public boolean waitForNthSelected(By locator, int index, boolean expectedSelected, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(driver -> {
            try {
                return driver.findElements(locator).get(index).isSelected() == expectedSelected;
            } catch (org.openqa.selenium.StaleElementReferenceException | IndexOutOfBoundsException e) {
                return false;
            }
        });
    }

    /**
     * Waits until an element's visible text is non-empty - the same
     * shell-renders-before-data pattern as waitForNonEmptyValue, but for
     * text content rather than an input's value attribute. Verified against
     * the live app: after Add Employee's Save navigates to Personal
     * Details, the name heading element is immediately present and visible
     * (an empty shell) while the actual employee data streams in a moment
     * later - checking isDisplayed()/getText() right away reads the empty
     * shell as success instead of waiting for the real content.
     */
    public boolean waitForNonEmptyText(By locator) {
        return wait.until(driver -> {
            try {
                String text = driver.findElement(locator).getText();
                return text != null && !text.trim().isEmpty();
            } catch (org.openqa.selenium.StaleElementReferenceException | org.openqa.selenium.NoSuchElementException e) {
                return false;
            }
        });
    }

    /**
     * Waits for document.readyState to equal "complete" - used after navigation
     * or full page reloads, not as a substitute for element-specific waits.
     */
    public void waitForPageLoad() {
        wait.until(webDriver ->
                ((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Waits for jQuery's active AJAX request count to reach zero.
     * Safe no-op (returns true immediately) on pages that don't use jQuery.
     */
    public void waitForAjax() {
        wait.until(webDriver -> {
            try {
                Object jqueryActive = ((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return (typeof jQuery !== 'undefined') ? jQuery.active : 0");
                return jqueryActive != null && jqueryActive.toString().equals("0");
            } catch (Exception e) {
                return true; // no jQuery on the page - nothing to wait for
            }
        });
    }
}
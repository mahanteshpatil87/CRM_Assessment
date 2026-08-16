package com.crmassessment.pages.base;

import com.crmassessment.utils.ElementActions;
import com.crmassessment.utils.LocatorUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Base class every Page Object extends. Holds one ElementActions instance
 * and exposes thin delegation methods only - no Selenium logic lives here
 * directly. This keeps Page Objects readable (this.click(x) instead of
 * this.elementActions.click(x)) while ElementActions remains the single
 * source of truth for how each interaction is actually performed.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final ElementActions elementActions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.elementActions = new ElementActions(driver);
    }

    protected void click(By locator) {
        elementActions.click(locator);
    }

    protected void type(By locator, String text) {
        elementActions.type(locator, text);
    }

    protected void doubleClick(By locator) {
        elementActions.doubleClick(locator);
    }

    protected void rightClick(By locator) {
        elementActions.rightClick(locator);
    }

    protected void hover(By locator) {
        elementActions.hover(locator);
    }

    protected void scrollToElement(By locator) {
        elementActions.scrollToElement(locator);
    }

    protected String getText(By locator) {
        return elementActions.getText(locator);
    }

    protected String getAttribute(By locator, String attributeName) {
        return elementActions.getAttribute(locator, attributeName);
    }

    protected boolean isDisplayed(By locator) {
        return elementActions.isDisplayed(locator);
    }

    protected boolean isSelected(By locator) {
        return elementActions.isSelected(locator);
    }

    protected boolean isEnabled(By locator) {
        return elementActions.isEnabled(locator);
    }

    protected void setCheckbox(By locator, boolean shouldBeChecked) {
        elementActions.setCheckbox(locator, shouldBeChecked);
    }

    protected void selectDropdownByVisibleText(By locator, String visibleText) {
        elementActions.selectDropdownByVisibleText(locator, visibleText);
    }

    protected void selectDropdownByValue(By locator, String value) {
        elementActions.selectDropdownByValue(locator, value);
    }

    protected String getSelectedDropdownText(By locator) {
        return elementActions.getSelectedDropdownText(locator);
    }

    protected void uploadFile(By locator, String absoluteFilePath) {
        elementActions.uploadFile(locator, absoluteFilePath);
    }

    protected List<WebElement> findAllVisible(By locator) {
        return elementActions.findAllVisible(locator);
    }

    protected int countElements(By locator) {
        return elementActions.countElements(locator);
    }

    protected List<String> getTextsOf(By locator) {
        return elementActions.getTextsOf(locator);
    }

    protected boolean waitForUrlMatches(String regex) {
        return elementActions.waitForUrlMatches(regex);
    }

    protected void typeOverAutoPopulatedValue(By locator, String text) {
        elementActions.typeOverAutoPopulatedValue(locator, text);
    }

    protected void waitForFormLoaderToDisappear() {
        elementActions.waitForFormLoaderToDisappear();
    }

    protected void waitForNonEmptyValue(By locator) {
        elementActions.waitForNonEmptyValue(locator);
    }

    protected void waitForNonEmptyText(By locator) {
        elementActions.waitForNonEmptyText(locator);
    }

    protected void waitForTextToBePresent(By locator, String text) {
        elementActions.waitForTextToBePresent(locator, text);
    }

    /**
     * Navigates directly to a given URL. Concrete pages rarely need this
     * themselves (navigation usually happens via clicking links/buttons),
     * but it's useful for the very first page load in a test (e.g. AdminLoginPage).
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Locates a plain text/password input by its visible label, scoped to
     * that label's containing .oxd-input-group. Needed across several
     * OrangeHRM forms (Add Employee's Employee Id, Add User's Username/
     * Password, Add Candidate's Email/Contact Number) whose inputs share
     * duplicate generic placeholders like "Type here" and carry no
     * id/name - the label is the only reliable anchor.
     */
    protected By inputForLabel(String fieldLabel) {
        return By.xpath(String.format(
                "//label[normalize-space()='%s']/ancestor::div[%s][1]//input",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    }

    /** The inline "Required" / field-specific validation message directly under a given field's label. */
    protected By validationMessageForLabel(String fieldLabel) {
        return By.xpath(String.format(
                "//label[normalize-space()='%s']/ancestor::div[%s][1]" +
                        "//*[contains(@class,'oxd-input-group__message')]",
                fieldLabel, LocatorUtils.INPUT_GROUP_CLASS_PREDICATE));
    }
}
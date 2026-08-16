package com.crmassessment.components;

import com.crmassessment.utils.ElementActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Drives the list-level bulk row-select checkboxes present on every
 * OrangeHRM results table (Employee List, Candidates, Pay Grades, Users):
 * a header "select all" checkbox plus one checkbox per row.
 * <p>
 * This is the framework's verified stand-in for the assignment's
 * multi-select requirement - no native multi-select/tag-picker widget
 * exists anywhere in the current public OrangeHRM demo (confirmed by DOM
 * inspection of the Pay Grade currency picker, PIM report field picker,
 * and Leave bulk entitlement assignment - all single-select-plus-repeat-add
 * or dropdown-filter patterns, not a selectable list).
 */
public class BulkCheckboxComponent {

    private final ElementActions elementActions;

    // The real <input type="checkbox"> renders with opacity:0 (verified against
    // the live DOM) - a styled sibling <span> provides the visible appearance.
    // Clicks target the enclosing <label> (visible, natively toggles the
    // input); state is read from the input itself via ElementActions'
    // presence-based (not visibility-based) isSelected/isNthSelected.
    private final By headerCheckboxLabel = By.cssSelector(".oxd-table-header .oxd-checkbox-wrapper label");
    private final By headerCheckboxInput = By.cssSelector(".oxd-table-header .oxd-checkbox-wrapper input[type='checkbox']");
    private final By rowCheckboxLabels = By.cssSelector(".oxd-table-body .oxd-checkbox-wrapper label");
    private final By rowCheckboxInputs = By.cssSelector(".oxd-table-body .oxd-checkbox-wrapper input[type='checkbox']");

    public BulkCheckboxComponent(WebDriver driver) {
        this.elementActions = new ElementActions(driver);
    }

    public void selectAllRows() {
        elementActions.click(headerCheckboxLabel);
    }

    public boolean isHeaderChecked() {
        return elementActions.isSelected(headerCheckboxInput);
    }

    public void toggleRow(int rowIndex) {
        elementActions.clickNth(rowCheckboxLabels, rowIndex);
    }

    public boolean isRowChecked(int rowIndex) {
        return elementActions.isNthSelected(rowCheckboxInputs, rowIndex);
    }

    public int getVisibleRowCount() {
        return elementActions.countElements(rowCheckboxInputs);
    }
}

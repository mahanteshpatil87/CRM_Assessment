package com.crmassessment.pages.pim;

import com.crmassessment.components.AutocompleteComponent;
import com.crmassessment.components.BulkCheckboxComponent;
import com.crmassessment.components.DropdownComponent;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.elements.pim.EmployeeListPageElements;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class EmployeeListPage extends EmployeeListPageElements {

    private static final String ROUTE = "/web/index.php/pim/viewEmployeeList";

    private final DropdownComponent dropdown;
    private final AutocompleteComponent autocomplete;
    private final BulkCheckboxComponent bulkCheckbox;

    public EmployeeListPage(WebDriver driver) {
        super(driver);
        this.dropdown = new DropdownComponent(driver);
        this.autocomplete = new AutocompleteComponent(driver);
        this.bulkCheckbox = new BulkCheckboxComponent(driver);
    }

    public void open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
    }

    public void searchByEmployeeName(String name) {
        autocomplete.selectFirstSuggestion("Employee Name", name);
        click(searchButton);
        // getResultRowCount() below has no wait of its own (a legitimate
        // zero-row result must not hang forever), and the form loader alone
        // is not a reliable "results re-rendered" signal - it may never
        // appear at all for a simple list search, in which case
        // waitForFormLoaderToDisappear() returns instantly and the very
        // next read can land on the stale pre-search row set (verified:
        // this produced both spurious 0 and spurious 16 row counts on
        // different runs). Waiting for the first row to actually contain
        // the searched name ties the wait to the real post-search state.
        waitForTextToBePresent(tableRows, name);
    }

    public void typeEmployeeName(String name) {
        autocomplete.type("Employee Name", name);
    }

    public boolean isNoMatchingEmployeeSuggested() {
        return autocomplete.isShowingNoRecordsFound();
    }

    public void filterByEmploymentStatus(String status) {
        dropdown.selectByLabel("Employment Status", status);
        click(searchButton);
        waitForFormLoaderToDisappear();
    }

    public void clickReset() {
        click(resetButton);
    }

    public int getResultRowCount() {
        return countElements(tableRows);
    }

    public List<String> getEmploymentStatusColumnValues() {
        return getTextsOf(employmentStatusColumn);
    }

    public List<String> getFirstNameColumnValues() {
        return getTextsOf(firstNameColumn);
    }

    /** PASS-evidence for "the searched employee actually appears in the results" - attaches a screenshot with the name row outlined. */
    public void captureFirstNameEvidence() {
        captureEvidence(firstNameColumn, "Newly created employee visible in Employee List search results");
    }

    public List<String> getLastNameColumnValues() {
        return getTextsOf(lastNameColumn);
    }

    public void selectAllRows() {
        bulkCheckbox.selectAllRows();
    }

    public boolean isHeaderCheckboxChecked() {
        return bulkCheckbox.isHeaderChecked();
    }

    public void toggleRow(int rowIndex) {
        bulkCheckbox.toggleRow(rowIndex);
    }

    public boolean isRowChecked(int rowIndex) {
        return bulkCheckbox.isRowChecked(rowIndex);
    }

    public int getVisibleRowCount() {
        return bulkCheckbox.getVisibleRowCount();
    }

    public AddEmployeePage clickAdd() {
        click(addButton);
        return new AddEmployeePage(driver);
    }
}

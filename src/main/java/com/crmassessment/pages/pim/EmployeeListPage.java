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

    public EmployeeListPage open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
        return this;
    }

    public EmployeeListPage searchByEmployeeName(String name) {
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
        return this;
    }

    public EmployeeListPage typeEmployeeName(String name) {
        autocomplete.type("Employee Name", name);
        return this;
    }

    public boolean isNoMatchingEmployeeSuggested() {
        return autocomplete.isShowingNoRecordsFound();
    }

    public EmployeeListPage filterByEmploymentStatus(String status) {
        dropdown.selectByLabel("Employment Status", status);
        click(searchButton);
        waitForFormLoaderToDisappear();
        return this;
    }

    public EmployeeListPage clickReset() {
        click(resetButton);
        return this;
    }

    public int getResultRowCount() {
        return countElements(tableRows);
    }

    public List<String> getEmploymentStatusColumnValues() {
        return getTextsOf(employmentStatusColumn);
    }

    /** PASS-evidence for "the Employment Status filter actually narrowed the results" - attaches a screenshot with a matching status cell outlined. */
    public EmployeeListPage captureEmploymentStatusEvidence() {
        captureEvidence(employmentStatusColumn, "Filtered Employment Status column visible in Employee List");
        return this;
    }

    public List<String> getFirstNameColumnValues() {
        return getTextsOf(firstNameColumn);
    }

    /** PASS-evidence for "the searched employee actually appears in the results" - attaches a screenshot with the name row outlined. */
    public EmployeeListPage captureFirstNameEvidence() {
        captureEvidence(firstNameColumn, "Newly created employee visible in Employee List search results");
        return this;
    }

    public List<String> getLastNameColumnValues() {
        return getTextsOf(lastNameColumn);
    }

    public EmployeeListPage selectAllRows() {
        bulkCheckbox.selectAllRows();
        return this;
    }

    public boolean isHeaderCheckboxChecked() {
        return bulkCheckbox.isHeaderChecked();
    }

    public EmployeeListPage toggleRow(int rowIndex) {
        bulkCheckbox.toggleRow(rowIndex);
        return this;
    }

    public boolean isRowChecked(int rowIndex) {
        return bulkCheckbox.isRowChecked(rowIndex);
    }

    public int getVisibleRowCount() {
        return bulkCheckbox.getVisibleRowCount();
    }

    /** PASS-evidence for "this specific row's checkbox is in the expected state" - attaches a screenshot with that row's checkbox outlined. */
    public EmployeeListPage captureRowCheckboxEvidence(int rowIndex, String description) {
        bulkCheckbox.captureRowEvidence(rowIndex, description);
        return this;
    }

    public AddEmployeePage clickAdd() {
        click(addButton);
        return new AddEmployeePage(driver);
    }
}

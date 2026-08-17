package com.crmassessment.pages.recruitment;

import com.crmassessment.components.AutocompleteComponent;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.elements.recruitment.CandidateListPageElements;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class CandidateListPage extends CandidateListPageElements {

    private static final String ROUTE = "/web/index.php/recruitment/viewCandidates";

    private final AutocompleteComponent autocomplete;

    public CandidateListPage(WebDriver driver) {
        super(driver);
        this.autocomplete = new AutocompleteComponent(driver);
    }

    public CandidateListPage open() {
        navigateTo(ConfigReader.getAppHost() + ROUTE);
        return this;
    }

    public CandidateListPage searchByCandidateName(String name) {
        autocomplete.selectFirstSuggestion("Candidate Name", name);
        click(searchButton);
        // The form loader alone is not a reliable "results re-rendered"
        // signal for a simple list search (see EmployeeListPage for the
        // full explanation) - wait for the actual searched value to appear
        // in the results instead.
        waitForTextToBePresent(candidateNameColumn, name);
        return this;
    }

    public int getResultRowCount() {
        return countElements(candidateNameColumn);
    }

    public List<String> getCandidateNameColumnValues() {
        return getTextsOf(candidateNameColumn);
    }

    public AddCandidatePage clickAdd() {
        click(addButton);
        return new AddCandidatePage(driver);
    }

    /** Opens the first result row's detail view. OrangeHRM reuses the Add Candidate route/component for this (.../addCandidate/{id}). */
    public CandidateDetailsPage openFirstResult() {
        click(firstViewIcon);
        return new CandidateDetailsPage(driver);
    }
}

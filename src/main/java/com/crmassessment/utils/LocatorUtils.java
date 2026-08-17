package com.crmassessment.utils;

/**
 * Shared XPath fragments for the label-anchored locator pattern used
 * throughout OrangeHRM's pages/components (BasePage, DropdownComponent,
 * FileUploadComponent, AutocompleteComponent) - none of OrangeHRM's form
 * fields carry id/name/aria-label, so every one of them is located via
 * its visible label's containing .oxd-input-group.
 */
public class LocatorUtils {

    private LocatorUtils() {
        // Utility class - no instances
    }

    /**
     * Whole-token XPath class match for "oxd-input-group". Plain
     * contains(@class, 'oxd-input-group') is a substring test, so it also
     * matches the child wrapper div "oxd-input-group__label-wrapper"
     * (which literally starts with "oxd-input-group") - and that wrapper
     * sits *closer* to the label than the real group and contains no
     * input, so any ancestor::div[...][1] search using the naive predicate
     * silently resolves to it instead of the real field group.
     */
    public static final String INPUT_GROUP_CLASS_PREDICATE =
            "contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')";
}

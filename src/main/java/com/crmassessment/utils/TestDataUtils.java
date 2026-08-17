package com.crmassessment.utils;

import com.crmassessment.config.ConfigReader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates test data (names, usernames, emails, ids). The public OrangeHRM
 * demo is a shared sandbox that other testers hit concurrently - both a
 * stale Employee Id and a reused Username have been observed to collide on
 * save (see: docs/framework-architecture.md), so every create-flow test
 * must generate its own unique data rather than relying on static fixtures.
 */
public class TestDataUtils {

    private static final String NAMES_SHEET = "EmployeeNames";
    private static List<Map<String, String>> namePairs;

    private TestDataUtils() {
        // Utility class - no instances
    }

    /** A short, time-based value unlikely to collide with concurrent test runs. */
    public static String uniqueSuffix() {
        return Long.toString(System.currentTimeMillis()).substring(5);
    }

    public static String uniqueValue(String prefix) {
        return prefix + uniqueSuffix();
    }

    public static String uniqueEmail(String localPartPrefix) {
        return prefixedLocalPart(localPartPrefix) + "@example.com";
    }

    private static String prefixedLocalPart(String localPartPrefix) {
        return (localPartPrefix + uniqueSuffix()).toLowerCase();
    }

    /**
     * A random {firstName, lastName} pair, real-looking rather than
     * synthetic (e.g. "Lucas Bennett" instead of "AutoQaEmp784512"), read
     * from test-data/excel/employeeNames.xlsx - loaded once and reused for
     * the life of the JVM. Kept in a spreadsheet rather than hardcoded in
     * Java so the pool is editable without a code change, the same
     * rationale as employeeTestData.xlsx (see TestDataProvider).
     */
    public static String[] randomNamePair() {
        List<Map<String, String>> pairs = loadNamePairs();
        Map<String, String> row = pairs.get(ThreadLocalRandom.current().nextInt(pairs.size()));
        return new String[]{row.get("firstName"), row.get("lastName")};
    }

    private static synchronized List<Map<String, String>> loadNamePairs() {
        if (namePairs == null) {
            namePairs = ExcelUtils.readSheet(ConfigReader.getEmployeeNamesFile(), NAMES_SHEET);
        }
        return namePairs;
    }

    /**
     * A username in the "three letters of first name + three letters of
     * last name + 3-digit number" convention (e.g. "lucben482") - a common
     * enterprise username scheme, and the digits still guarantee
     * uniqueness against the shared demo's other accounts.
     */
    public static String generateUsername(String firstName, String lastName) {
        String digits = String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
        return (firstLetters(firstName, 3) + firstLetters(lastName, 3) + digits).toLowerCase();
    }

    private static String firstLetters(String value, int count) {
        String lettersOnly = value.replaceAll("[^A-Za-z]", "");
        return lettersOnly.substring(0, Math.min(count, lettersOnly.length()));
    }
}

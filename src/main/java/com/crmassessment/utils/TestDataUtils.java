package com.crmassessment.utils;

import com.crmassessment.config.ConfigReader;

import java.time.LocalDate;
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

    /**
     * A short, time-based value unlikely to collide with concurrent test
     * runs. Uses nanoTime() rather than currentTimeMillis(): Windows' timer
     * resolution is coarse (~15ms), so two calls microseconds apart (e.g.
     * one for an Employee Id, one for a last name, in the same test method)
     * could return the exact same millisecond and therefore the same
     * suffix. nanoTime()'s much finer resolution keeps back-to-back calls
     * independent.
     */
    public static String uniqueSuffix() {
        return String.format("%05d", Math.abs(System.nanoTime()) % 100_000);
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

    private static final String[] MARITAL_STATUSES = {"Single", "Married"};
    private static final String[] GENDERS = {"Male", "Female"};
    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    public static String randomMaritalStatus() {
        return MARITAL_STATUSES[ThreadLocalRandom.current().nextInt(MARITAL_STATUSES.length)];
    }

    public static String randomGender() {
        return GENDERS[ThreadLocalRandom.current().nextInt(GENDERS.length)];
    }

    public static String randomBloodType() {
        return BLOOD_TYPES[ThreadLocalRandom.current().nextInt(BLOOD_TYPES.length)];
    }

    public static String randomDriversLicenseNumber() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000));
    }

    /**
     * A random past Date of Birth (22-58 years old), in the "yyyy-dd-mm"
     * format OrangeHRM's own field expects (day before month, not the
     * usual yyyy-mm-dd).
     */
    public static String randomDateOfBirth() {
        int age = ThreadLocalRandom.current().nextInt(22, 59);
        return formatYyyyDdMm(LocalDate.now().minusYears(age).minusDays(ThreadLocalRandom.current().nextInt(365)));
    }

    /** A random future License Expiry Date (1-5 years out), same yyyy-dd-mm format as Date of Birth. */
    public static String randomLicenseExpiryDate() {
        return formatYyyyDdMm(LocalDate.now().plusYears(ThreadLocalRandom.current().nextInt(1, 6)));
    }

    private static String formatYyyyDdMm(LocalDate date) {
        return String.format("%04d-%02d-%02d", date.getYear(), date.getDayOfMonth(), date.getMonthValue());
    }
}

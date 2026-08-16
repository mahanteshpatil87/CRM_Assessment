package com.crmassessment.utils;

/**
 * Generates unique values for test data (names, usernames, emails, ids).
 * The public OrangeHRM demo is a shared sandbox that other testers hit
 * concurrently - both a stale Employee Id and a reused Username have been
 * observed to collide on save (see: docs/framework-architecture.md), so
 * every create-flow test must generate its own unique data rather than
 * relying on static fixtures.
 */
public class TestDataUtils {

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
}

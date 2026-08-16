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

    // A small pool of plausible real first names, used wherever a test needs
    // a person's name to actually look like one rather than a prefix+digits
    // QA string (e.g. "AutoQaEmp784512"). Picked from, not generated, so the
    // visible name is always clean - any uniqueness a given test still needs
    // (e.g. to keep a shared-demo search deterministic) is carried on a
    // different field instead, see uniqueValue's callers.
    private static final String[] FIRST_NAMES = {
            "Ethan", "Olivia", "Noah", "Ava", "Liam", "Sophia", "Mason", "Isabella",
            "Lucas", "Mia", "Elijah", "Amelia", "James", "Harper", "Benjamin", "Evelyn"
    };

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

    /** A plausible real first name, picked at random from a fixed pool - see FIRST_NAMES. */
    public static String randomFirstName() {
        return FIRST_NAMES[java.util.concurrent.ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
    }

    public static String uniqueEmail(String localPartPrefix) {
        return prefixedLocalPart(localPartPrefix) + "@example.com";
    }

    private static String prefixedLocalPart(String localPartPrefix) {
        return (localPartPrefix + uniqueSuffix()).toLowerCase();
    }
}

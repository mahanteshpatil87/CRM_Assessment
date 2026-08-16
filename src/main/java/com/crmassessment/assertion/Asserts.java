package com.crmassessment.assertion;

/**
 * Facade tests call directly. Delegates to HardAsserts or SoftAsserts
 * based on the AssertionType parameter, so tests use one consistent API
 * regardless of which underlying behavior they need.
 */
public class Asserts {

    private final HardAsserts hardAsserts = new HardAsserts();
    private final SoftAsserts softAsserts = new SoftAsserts();

    public void assertEquals(Object actual, Object expected, String message, AssertionType type) {
        if (type == AssertionType.HARD) {
            hardAsserts.assertEquals(actual, expected, message);
        } else {
            softAsserts.assertEquals(actual, expected, message);
        }
    }

    public void assertTrue(boolean condition, String message, AssertionType type) {
        if (type == AssertionType.HARD) {
            hardAsserts.assertTrue(condition, message);
        } else {
            softAsserts.assertTrue(condition, message);
        }
    }

    public void assertFalse(boolean condition, String message, AssertionType type) {
        if (type == AssertionType.HARD) {
            hardAsserts.assertFalse(condition, message);
        } else {
            softAsserts.assertFalse(condition, message);
        }
    }

    public void assertNotNull(Object object, String message, AssertionType type) {
        if (type == AssertionType.HARD) {
            hardAsserts.assertNotNull(object, message);
        } else {
            softAsserts.assertNotNull(object, message);
        }
    }

    /**
     * Flushes any accumulated SOFT assertion failures, causing the test to
     * fail if any were recorded. Has no effect on HARD assertions, since
     * those already stopped the test immediately at the point of failure.
     * Must be called at the end of any test method that uses SOFT asserts.
     */
    public void assertAll() {
        softAsserts.assertAll();
    }
}
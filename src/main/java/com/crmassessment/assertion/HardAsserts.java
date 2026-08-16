package com.crmassessment.assertion;

import com.crmassessment.reports.ExtentManager;
import com.crmassessment.utils.ScreenshotUtils;
import org.testng.Assert;

/**
 * Wraps TestNG's Assert. Fails immediately and stops the test on failure,
 * but first logs the result to ExtentReports and captures a screenshot.
 */
public class HardAsserts {

    public void assertEquals(Object actual, Object expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            ExtentManager.getTest().pass("PASS: " + message);
        } catch (AssertionError e) {
            ExtentManager.getTest().fail(
                    "FAIL: " + message + " | Expected: " + expected + " | Actual: " + actual);
            ScreenshotUtils.captureAndAttach(message);
            throw e;
        }
    }

    public void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition, message);
            ExtentManager.getTest().pass("PASS: " + message);
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("FAIL: " + message);
            ScreenshotUtils.captureAndAttach(message);
            throw e;
        }
    }

    public void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition, message);
            ExtentManager.getTest().pass("PASS: " + message);
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("FAIL: " + message);
            ScreenshotUtils.captureAndAttach(message);
            throw e;
        }
    }

    public void assertNotNull(Object object, String message) {
        try {
            Assert.assertNotNull(object, message);
            ExtentManager.getTest().pass("PASS: " + message);
        } catch (AssertionError e) {
            ExtentManager.getTest().fail("FAIL: " + message);
            ScreenshotUtils.captureAndAttach(message);
            throw e;
        }
    }
}
package com.crmassessment.listeners;

import com.crmassessment.reports.ExtentManager;
import com.crmassessment.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Hooks into the TestNG test lifecycle automatically (registered via
 * testng.xml <listeners>, not called directly by test classes).
 * Creates one ExtentTest per test method, logs the outcome, and flushes
 * the report once the whole suite finishes.
 */
public class TestNGListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentManager.createTest(testName, description != null ? description : testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().pass("Test completed successfully.");
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String failureMessage = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Test failed with no exception message.";
        ExtentManager.getTest().fail("Test failed: " + failureMessage);
        ScreenshotUtils.captureAndAttach(result.getMethod().getMethodName() + " - failure");
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "No reason provided.";
        ExtentManager.getTest().skip("Test skipped: " + reason);
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }
}
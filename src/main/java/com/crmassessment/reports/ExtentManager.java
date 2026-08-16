package com.crmassessment.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton owner of the single shared ExtentReports instance for the run,
 * plus a ThreadLocal<ExtentTest> so each test method writes to its own
 * report entry - required for correctness once parallel execution is enabled,
 * since concurrent threads must not write into the same ExtentTest.
 */
public class ExtentManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentManager() {
        // Utility/singleton holder - no instances
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = System.getProperty("user.dir")
                    + File.separator + "reports"
                    + File.separator + "ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("CRM_Assessment Automation Report");
            sparkReporter.config().setReportName("OrangeHRM Admin - Test Execution Report");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Project", "CRM_Assessment");
            extentReports.setSystemInfo("Tester", "SDET Assessment");
        }
        return extentReports;
    }

    public static void createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTestThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            throw new IllegalStateException(
                    "ExtentTest has not been created for this thread. Call ExtentManager.createTest() first.");
        }
        return test;
    }

    public static void removeTest() {
        extentTestThreadLocal.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
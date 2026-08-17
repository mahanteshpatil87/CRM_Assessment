package com.crmassessment.base;

import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.pages.AllPages;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test class extends this. Wires DriverManager and AssertsManager
 * lifecycle around each test method - init before, teardown after.
 * Reporting (ExtentTest creation/closing) is handled separately by
 * TestNGListener, not here, since that's a cross-cutting concern
 * registered at the suite level (testng.xml), not per-test setup.
 */
public abstract class BaseTest {

    /** Every page object for this test, built once the driver is ready - see AllPages. */
    protected AllPages pages;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.initDriver();
        AssertsManager.initAsserts();
        DriverManager.getDriver().get(ConfigReader.getBaseUrl());
        pages = new AllPages(DriverManager.getDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Flush any accumulated SOFT assertion failures before the
        // Asserts instance is discarded, so they actually fail the test.
        AssertsManager.getAsserts().assertAll();
        AssertsManager.removeAsserts();
        DriverManager.quitDriver();
    }
}
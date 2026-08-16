package com.crmassessment.driver;

import com.crmassessment.config.ConfigReader;
import org.openqa.selenium.WebDriver;

/**
 * Owns the ThreadLocal<WebDriver> and its lifecycle: init, get, quit.
 * ThreadLocal is used (not a plain static field) so that if TestNG is later
 * configured for parallel execution (parallel="methods" in testng.xml),
 * each thread gets its own isolated WebDriver instance - no two tests
 * running concurrently can ever share or collide on the same browser session.
 * This costs nothing today (single-threaded runs work identically) and
 * removes a whole class of flaky-parallel-test bugs later.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Utility class - no instances
    }

    public static void initDriver() {
        BrowserType browserType = BrowserType.fromString(ConfigReader.getBrowser());
        WebDriver driver = DriverFactory.createDriver(browserType);
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialized for this thread. Call DriverManager.initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
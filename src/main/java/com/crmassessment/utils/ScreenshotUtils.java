package com.crmassessment.utils;

import com.crmassessment.driver.DriverManager;
import com.crmassessment.reports.ExtentManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Captures a screenshot from the current thread's WebDriver and attaches it
 * to that thread's current ExtentTest entry. Called from HardAsserts,
 * SoftAsserts, and TestNGListener on failure.
 */
public class ScreenshotUtils {

    private ScreenshotUtils() {
        // Utility class - no instances
    }

    public static String captureAndAttach(String failureContext) {
        try {
            WebDriver driver = DriverManager.getDriver();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = "Failure_" + timestamp + ".png";
            String screenshotDir = System.getProperty("user.dir") + File.separator + "screenshots";
            String fullPath = screenshotDir + File.separator + fileName;

            Files.createDirectories(Paths.get(screenshotDir));

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), Paths.get(fullPath));

            ExtentManager.getTest().addScreenCaptureFromPath(fullPath, failureContext);
            return fullPath;
        } catch (IOException | IllegalStateException e) {
            // IllegalStateException covers "driver not initialized" - don't let a
            // screenshot failure mask the original assertion failure that triggered this.
            ExtentManager.getTest().warning("Could not capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
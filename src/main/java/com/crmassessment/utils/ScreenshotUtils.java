package com.crmassessment.utils;

import com.crmassessment.driver.DriverManager;
import com.crmassessment.reports.ExtentManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Captures a screenshot from the current thread's WebDriver and attaches it
 * to that thread's current ExtentTest entry. Called from HardAsserts,
 * SoftAsserts, and TestNGListener on failure - and from ElementActions for
 * PASS-side visual evidence (see captureHighlightedAndAttach).
 */
public class ScreenshotUtils {

    private ScreenshotUtils() {
        // Utility class - no instances
    }

    public static String captureAndAttach(String failureContext) {
        try {
            WebDriver driver = DriverManager.getDriver();
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fullPath = writeScreenshotFile(srcFile, "Failure");

            ExtentManager.getTest().addScreenCaptureFromPath(fullPath, failureContext);
            return fullPath;
        } catch (IOException | IllegalStateException e) {
            // IllegalStateException covers "driver not initialized" - don't let a
            // screenshot failure mask the original assertion failure that triggered this.
            ExtentManager.getTest().warning("Could not capture screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Captures full-page visual evidence for a PASSING validation, with the
     * specific element being validated outlined so it's obvious at a glance
     * what the screenshot is proving - e.g. the newly created employee's
     * name after an "Add Employee" test, or the username row after an
     * "Add User" search. Without this, the HTML report only shows text
     * assertions for passing tests; a reviewer has to trust the log line
     * rather than see the actual UI state that made it pass.
     * <p>
     * The highlight is applied and removed via a direct style attribute
     * (not a CSS class), so it never touches the application's own
     * stylesheet and cannot affect any other element on the page.
     */
    public static String captureHighlightedAndAttach(WebDriver driver, WebElement element, String description) {
        String originalStyle = element.getAttribute("style");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "arguments[0].scrollIntoView({block:'center'});"
                            + "arguments[0].style.outline='3px solid #ff2d55';"
                            + "arguments[0].style.boxShadow='0 0 0 4px rgba(255,45,85,0.35)';",
                    element);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fullPath = writeScreenshotFile(srcFile, "Evidence");

            ExtentManager.getTest().pass(description);
            ExtentManager.getTest().addScreenCaptureFromPath(fullPath, description);
            return fullPath;
        } catch (IOException | IllegalStateException e) {
            ExtentManager.getTest().warning("Could not capture evidence screenshot: " + e.getMessage());
            return null;
        } finally {
            // Always restore the element's original style, pass or fail, so a
            // highlight never leaks into whatever the test does next with this page.
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].setAttribute('style', arguments[1]);",
                    element, originalStyle == null ? "" : originalStyle);
        }
    }

    private static String writeScreenshotFile(File srcFile, String prefix) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String fileName = prefix + "_" + timestamp + ".png";
        String screenshotDir = System.getProperty("user.dir") + File.separator + "screenshots";
        String fullPath = screenshotDir + File.separator + fileName;

        Files.createDirectories(Paths.get(screenshotDir));
        Files.copy(srcFile.toPath(), Paths.get(fullPath));
        return fullPath;
    }
}
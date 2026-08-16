package com.crmassessment.driver;

import com.crmassessment.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible only for CREATING a correctly configured WebDriver instance
 * for a given browser type. Does not manage lifecycle/threading - that's
 * DriverManager's job. Kept separate so each class has one responsibility.
 */
public class DriverFactory {

    private DriverFactory() {
        // Utility class - no instances
    }

    public static WebDriver createDriver(BrowserType browserType) {
        WebDriver driver;
        boolean headless = ConfigReader.isHeadless();

        switch (browserType) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    // window.maximize() has no real display to maximize against in
                    // headless mode and silently falls back to a small default
                    // viewport (verified: this triggered OrangeHRM's mobile-responsive
                    // layout - collapsed filter panels, card-based lists instead of
                    // tables - which is why several locators built against the desktop
                    // layout timed out). Force a real desktop viewport at launch.
                    options.addArguments("--window-size=1920,1080");
                }
                options.addArguments("--remote-allow-origins=*");

                // Reduce automation fingerprint that triggers bot-detection (e.g. Cloudflare)
                // on target sites. Standard practice for QA automation - not bypassing any
                // authentication or security control, just not over-announcing automation.
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-blink-features=AutomationControlled");

                // Downloads (e.g. verifying a resume attachment) land in a known,
                // project-relative directory instead of the OS default Downloads
                // folder, so FileUtils can find and assert against them.
                String downloadDir = new File(ConfigReader.getDownloadDir()).getAbsolutePath();
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("download.default_directory", downloadDir);
                prefs.put("download.prompt_for_download", false);
                prefs.put("plugins.always_open_pdf_externally", true);
                options.setExperimentalOption("prefs", prefs);

                driver = new ChromeDriver(options);
            }

            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                driver = new EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getImplicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getPageLoadTimeoutSeconds()));

        if (headless) {
            // Belt-and-suspenders alongside --window-size: guarantees a real
            // desktop viewport even if a browser/driver combination ignores
            // the startup argument on the very first page load.
            driver.manage().window().setSize(new Dimension(1920, 1080));
        } else {
            driver.manage().window().maximize();
        }

        return driver;
    }
}
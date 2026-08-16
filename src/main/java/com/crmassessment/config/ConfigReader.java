package com.crmassessment.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties once from the classpath and exposes typed getters.
 * Every value can be overridden at runtime via -D system properties
 * (e.g. -Dbrowser=firefox), which take precedence over the file.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    // Static initializer: loads the file exactly once, when this class is first used.
    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath at config/config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
        // Utility class - no instances
    }

    private static String get(String key) {
        // System property (-Dkey=value) always wins over the file, if present
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing required config key: " + key);
        }
        return value;
    }

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    /**
     * The application's scheme+host (e.g. https://opensource-demo.orangehrmlive.com),
     * derived from baseUrl rather than configured separately, so page objects can
     * build their own SPA route (e.g. /web/index.php/pim/viewEmployeeList) without
     * ever hardcoding the host.
     */
    public static String getAppHost() {
        String baseUrl = getBaseUrl();
        int pathStart = baseUrl.indexOf("/web/");
        return pathStart > 0 ? baseUrl.substring(0, pathStart) : baseUrl;
    }

    public static String getBrowser() {
        // Maps to the "browser" systemPropertyVariable set by Surefire from pom.xml's test.browser
        return get("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int getImplicitWaitSeconds() {
        return Integer.parseInt(get("implicitWaitSeconds"));
    }

    public static int getExplicitTimeoutSeconds() {
        return Integer.parseInt(get("explicitTimeoutSeconds"));
    }

    public static int getPageLoadTimeoutSeconds() {
        return Integer.parseInt(get("pageLoadTimeoutSeconds"));
    }

    public static String getAdminUsername() {
        return get("adminUsername");
    }

    public static String getAdminPassword() {
        return get("adminPassword");
    }

    public static String getValidResumeFilePath() {
        return get("validResumeFilePath");
    }

    public static String getInvalidResumeFilePath() {
        return get("invalidResumeFilePath");
    }

    public static String getDownloadDir() {
        return get("downloadDir");
    }

    public static String getEmployeeTestDataFile() {
        return get("employeeTestDataFile");
    }
}
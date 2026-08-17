package com.crmassessment.tests.admin;

import com.crmassessment.assertion.AssertionType;
import com.crmassessment.assertion.AssertsManager;
import com.crmassessment.base.BaseTest;
import com.crmassessment.config.ConfigReader;
import com.crmassessment.driver.DriverManager;
import org.testng.annotations.Test;

public class AdminLoginTests extends BaseTest {

    @Test(description = "TC-001: Valid Admin credentials log the user in and land on the Dashboard")
    public void validLoginNavigatesToDashboard() {

        pages.adminLoginPage
                .loginAs(ConfigReader.getAdminUsername(), ConfigReader.getAdminPassword());

        AssertsManager.getAsserts().assertTrue(
                pages.adminDashboardPage
                        .isDisplayed(),
                "Dashboard header should be visible after a valid login", AssertionType.HARD);

        AssertsManager.getAsserts().assertTrue(
                pages.adminDashboardPage
                        .getCurrentUrl().contains("/dashboard/index"),
                "URL should navigate to the dashboard route after a valid login", AssertionType.HARD);

        pages.adminDashboardPage
                .captureDashboardEvidence();
    }

    @Test(description = "TC-002: Invalid credentials are rejected with an inline error and no session is created")
    public void invalidLoginShowsErrorAndStaysOnLoginPage() {
        pages.adminLoginPage
                .loginAs("InvalidUser123", "WrongPassword999");

        AssertsManager.getAsserts().assertTrue(
                pages.adminLoginPage.isValidationErrorDisplayed(), "An error alert should be displayed for invalid credentials", AssertionType.HARD);
        AssertsManager.getAsserts().assertEquals(
                pages.adminLoginPage.getValidationErrorText(), "Invalid credentials",
                "Error text should read 'Invalid credentials'", AssertionType.HARD);
        AssertsManager.getAsserts().assertTrue(
                DriverManager.getDriver().getCurrentUrl().contains("/auth/login"),
                "Should remain on the login route after a failed login", AssertionType.HARD);

        pages.adminLoginPage
                .captureValidationErrorEvidence();
    }

    @Test(description = "TC-AUTH-004: Logging out from an active session returns to the login page and cannot be undone with Back")
    public void logoutEndsSessionAndBlocksBackNavigation() {
        pages.adminLoginPage
                .loginAs(ConfigReader.getAdminUsername(), ConfigReader.getAdminPassword());

        pages.adminDashboardPage
                .logout();

        AssertsManager.getAsserts().assertTrue(
                DriverManager.getDriver().getCurrentUrl().contains("/auth/login"),
                "Logout should return to the login route", AssertionType.HARD);

        // The browser's back-forward cache can repaint the last-seen dashboard
        // DOM/URL from memory without any new request (verified: right after
        // Back, the URL alone can still read /dashboard even though the
        // session is gone). A refresh bypasses bfcache and forces a real
        // request, which is what actually proves whether the session is dead.
        DriverManager.getDriver().navigate().back();
        DriverManager.getDriver().navigate().refresh();
        AssertsManager.getAsserts().assertTrue(
                DriverManager.getDriver().getCurrentUrl().contains("/auth/login"),
                "After Back + a real page refresh, an unauthenticated session must redirect to login, not stay on the dashboard", AssertionType.HARD);

        pages.adminLoginPage
                .captureLoginFormEvidence();
    }
}

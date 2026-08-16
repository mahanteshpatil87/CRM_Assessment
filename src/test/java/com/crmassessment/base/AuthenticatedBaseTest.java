package com.crmassessment.base;

import com.crmassessment.config.ConfigReader;
import com.crmassessment.driver.DriverManager;
import com.crmassessment.pages.admin.AdminLoginPage;
import org.testng.annotations.BeforeMethod;

/**
 * Base for every test that needs an authenticated session before it starts
 * (i.e. everything except AdminLoginTests, which tests login itself).
 * TestNG runs superclass @BeforeMethod methods (BaseTest.setUp - driver
 * init + navigation to the login page) before this subclass's, so the
 * driver is always ready before loginAsAdmin runs.
 */
public abstract class AuthenticatedBaseTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void loginAsAdmin() {
        new AdminLoginPage(DriverManager.getDriver())
                .loginAs(ConfigReader.getAdminUsername(), ConfigReader.getAdminPassword());
    }
}

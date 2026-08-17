package com.crmassessment.base;

import com.crmassessment.config.ConfigReader;
import org.testng.annotations.BeforeMethod;

/**
 * Base for every test that needs an authenticated session before it starts
 * (i.e. everything except AdminLoginTests, which tests login itself).
 * TestNG runs superclass @BeforeMethod methods (BaseTest.setUp - driver
 * init, navigation to the login page, and building `pages`) before this
 * subclass's, so both the driver and `pages` are ready before loginAsAdmin runs.
 * <p>
 * Deliberately NOT alwaysRun: unlike BaseTest.tearDown() (a cleanup step
 * that must always attempt to run), this is a setup step that depends on
 * BaseTest.setUp() having already succeeded (driver ready, `pages` built).
 * If setUp() fails, TestNG correctly skips this method and the test
 * itself rather than this throwing a confusing secondary NullPointerException
 * on a null `pages` that masks the real, original failure.
 */
public abstract class AuthenticatedBaseTest extends BaseTest {

    @BeforeMethod
    public void loginAsAdmin() {
        pages.adminLoginPage
                .loginAs(ConfigReader.getAdminUsername(), ConfigReader.getAdminPassword());
    }
}

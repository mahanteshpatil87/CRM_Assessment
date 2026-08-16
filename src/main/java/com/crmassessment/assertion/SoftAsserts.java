package com.crmassessment.assertion;

import com.crmassessment.reports.ExtentManager;
import com.crmassessment.utils.ScreenshotUtils;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

/**
 * Extends TestNG's SoftAssert. Collects failures instead of stopping the
 * test immediately. Every failure is still logged + screenshotted here;
 * assertAll() must be called at the end of the test method to actually
 * surface any accumulated failures.
 */
public class SoftAsserts extends SoftAssert {

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        ExtentManager.getTest().pass("PASS: " + assertCommand.getMessage());
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        ExtentManager.getTest().fail("FAIL: " + assertCommand.getMessage());
        ScreenshotUtils.captureAndAttach(assertCommand.getMessage());
    }
}
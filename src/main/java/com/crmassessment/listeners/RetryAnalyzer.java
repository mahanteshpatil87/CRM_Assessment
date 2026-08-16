package com.crmassessment.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * A single-retry {@link IRetryAnalyzer}, applied per test via
 * {@code @Test(retryAnalyzer = RetryAnalyzer.class)} - not suite-wide by
 * default. Genuine failures should surface as failures, not be silently
 * retried into false passes; this is attached only to the specific tests
 * empirically observed to fail from transient live-demo/network noise
 * (identical code passing repeatedly then failing once, alongside an
 * observed WebDriver connection reset - not a deterministic locator or
 * wait-condition defect). See docs/framework-architecture.md's
 * "Known technical debt" section for the evidence and the exact test list.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            return true;
        }
        return false;
    }
}

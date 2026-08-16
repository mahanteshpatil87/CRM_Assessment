package com.crmassessment.assertion;

/**
 * ThreadLocal lifecycle manager for Asserts - mirrors DriverManager's pattern.
 * SoftAssert accumulates state internally, so under parallel test execution
 * a shared instance would let one thread's failures bleed into another's.
 * Each thread gets its own fresh Asserts instance per test method.
 */
public class AssertsManager {

    private static final ThreadLocal<Asserts> assertsThreadLocal = new ThreadLocal<>();

    private AssertsManager() {
        // Utility class - no instances
    }

    public static void initAsserts() {
        assertsThreadLocal.set(new Asserts());
    }

    public static Asserts getAsserts() {
        Asserts asserts = assertsThreadLocal.get();
        if (asserts == null) {
            throw new IllegalStateException(
                    "Asserts has not been initialized for this thread. Call AssertsManager.initAsserts() first.");
        }
        return asserts;
    }

    public static void removeAsserts() {
        assertsThreadLocal.remove();
    }
}
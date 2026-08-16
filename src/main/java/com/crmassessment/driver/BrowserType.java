package com.crmassessment.driver;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType fromString(String value) {
        try {
            return BrowserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser: '" + value + "'. Supported values: chrome, firefox, edge");
        }
    }
}
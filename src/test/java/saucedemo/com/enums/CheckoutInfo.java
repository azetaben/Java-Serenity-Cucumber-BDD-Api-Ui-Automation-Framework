package saucedemo.com.enums;

import saucedemo.com.models.CheckoutDetails;

import java.util.Optional;

public enum CheckoutInfo {
    VALID_CUSTOMER("Alex", "Taylor", "SW1A 1AA", "checkout overview", null),
    VALID_HYPHENATED_NAME("Anne-Marie", "Smith-Jones", "SW1A 1AA", "checkout overview", null),
    VALID_APOSTROPHE_NAME("Sean", "O'Connor", "D02 X285", "checkout overview", null),
    VALID_UNICODE_NAME("Zoë", "García", "75001", "checkout overview", null),
    VALID_NUMERIC_POSTAL_CODE("Jordan", "Lee", "90210", "checkout overview", null),
    VALID_LEADING_ZERO_POSTAL_CODE("Jamie", "Brown", "00123", "checkout overview", null),
    MISSING_FIRST_NAME("", "Taylor", "SW1A 1AA", "checkout information", "Error: First Name is required"),
    MISSING_LAST_NAME("Alex", "", "SW1A 1AA", "checkout information", "Error: Last Name is required"),
    MISSING_POSTAL_CODE("Alex", "Taylor", "", "checkout information", "Error: Postal Code is required"),
    MISSING_FIRST_AND_LAST_NAME("", "", "SW1A 1AA", "checkout information", "Error: First Name is required"),
    MISSING_FIRST_NAME_AND_POSTAL_CODE("", "Taylor", "", "checkout information", "Error: First Name is required"),
    MISSING_LAST_NAME_AND_POSTAL_CODE("Alex", "", "", "checkout information", "Error: Last Name is required"),
    ALL_FIELDS_EMPTY("", "", "", "checkout information", "Error: First Name is required");
    private final CheckoutDetails details;
    private final String expectedPage, error;

    CheckoutInfo(String first, String last, String postal, String page, String error) {
        this.details = new CheckoutDetails(first, last, postal);
        this.expectedPage = page;
        this.error = error;
    }

    public static CheckoutInfo from(String text) {
        String key = text.trim().replace('-', '_').replace(' ', '_').toUpperCase();
        try {
            return valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown checkout data case: " + text, e);
        }
    }

    public CheckoutDetails details() {
        return details;
    }

    public String getExpectedPage() {
        return expectedPage;
    }

    public Optional<String> getExpectedError() {
        return Optional.ofNullable(error);
    }
}

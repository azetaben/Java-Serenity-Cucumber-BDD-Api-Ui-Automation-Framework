package saucedemo.com.models;

import java.util.Map;

public record CheckoutDetails(String firstName, String lastName, String postalCode) {
    public CheckoutDetails {
        firstName = firstName == null ? "" : firstName;
        lastName = lastName == null ? "" : lastName;
        postalCode = postalCode == null ? "" : postalCode;
    }

    public static CheckoutDetails fromRow(Map<String, String> row) {
        return new CheckoutDetails(row.getOrDefault("firstName", row.getOrDefault("first_name", "")),
                row.getOrDefault("lastName", row.getOrDefault("last_name", "")),
                row.getOrDefault("postalCode", row.getOrDefault("postal_code", "")));
    }
}

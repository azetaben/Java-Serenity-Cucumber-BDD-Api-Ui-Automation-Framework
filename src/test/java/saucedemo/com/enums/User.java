package saucedemo.com.enums;

import java.util.Arrays;
import java.util.Optional;

public enum User {
    STANDARD_USER("standard_user", "standard", "inventory", null),
    LOCKED_OUT_USER("locked_out_user", "locked", "login", "Epic sadface: Sorry, this user has been locked out."),
    PROBLEM_USER("problem_user", "problem", "inventory", null),
    PERFORMANCE_GLITCH_USER("performance_glitch_user", "performance", "inventory", null),
    ERROR_USER("error_user", "error", "inventory", null),
    VISUAL_USER("visual_user", "visual", "inventory", null);

    private final String username, alias, expectedPage, error;

    User(String username, String alias, String expectedPage, String error) {
        this.username = username;
        this.alias = alias;
        this.expectedPage = expectedPage;
        this.error = error;
    }

    public static User fromAlias(String value) {
        String normalized = value.trim().toLowerCase().replace('-', '_').replace(' ', '_');
        return Arrays.stream(values()).filter(user -> user.alias.equals(normalized) || user.username.equals(normalized))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown SauceDemo user: " + value));
    }

    public String getUsername() {
        return this == STANDARD_USER
                ? saucedemo.com.properties.ApplicationConfig.required("standard_user.username")
                : username;
    }

    public String getPassword() {
        return saucedemo.com.properties.ApplicationConfig.required("all_users.password");
    }

    public String getExpectedPage() {
        return expectedPage;
    }

    public Optional<String> getExpectedLoginError() {
        return Optional.ofNullable(error);
    }
}

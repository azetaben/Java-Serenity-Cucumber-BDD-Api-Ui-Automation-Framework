package saucedemo.com.utilities;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.models.CheckoutDetails;
import saucedemo.com.models.LoginAccount;
import saucedemo.com.properties.ApplicationConfig;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Synthetic SauceDemo data. Create one instance per scenario; replay with the same seed and call order.
 * Successful login credentials come from configuration, never from randomly invented accounts.
 */
public final class TestDataGeneration {
    private static final Logger log = LoggerFactory.getLogger(TestDataGeneration.class);
    private final Faker faker;

    public TestDataGeneration(long seed) {
        faker = new Faker(Locale.UK, new Random(seed));
        log.debug("Initialize scenario test data with seed {}", seed);
    }

    public static TestDataGeneration configured() {
        String seed = ApplicationConfig.required("framework.faker.seed");
        try {
            return new TestDataGeneration(Long.parseLong(seed.trim()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("faker.seed must be a valid long integer", exception);
        }
    }

    private static String normalize(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Test data case must not be blank");
        }
        return name.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    public LoginAccount login(String caseName) {
        String name = normalize(caseName);
        log.debug("Generate login data for case {}", name);
        return switch (name) {
            case "standard", "problem", "performance", "error", "visual" -> LoginAccount.fromAlias(name);
            case "locked" -> LoginAccount.fromAlias(name);
            case "invalid_username" -> new LoginAccount(unknownUsername(), standard().password());
            case "invalid_password" -> new LoginAccount(standard().username(), incorrectPassword());
            case "invalid_credentials" -> new LoginAccount(unknownUsername(), incorrectPassword());
            case "missing_username" -> new LoginAccount("", standard().password());
            case "missing_password" -> new LoginAccount(standard().username(), "");
            case "empty" -> new LoginAccount("", "");
            default -> throw new IllegalArgumentException("Unsupported login data case: " + caseName);
        };
    }

    /**
     * Positive means accepted by this form's non-empty-field checks, not a verified customer/address.
     * Negative cases omit fields; the application does not enforce postal-code format or name lengths.
     */
    public CheckoutDetails checkout(String caseName) {
        String name = normalize(caseName);
        // Reject misspellings before consuming random values.
        if (!List.of("valid", "hyphenated", "apostrophe", "leading_zero", "missing_first_name",
                "missing_last_name", "missing_postal_code", "missing_first_and_last_name",
                "missing_first_name_and_postal_code", "missing_last_name_and_postal_code", "empty").contains(name)) {
            throw new IllegalArgumentException("Unsupported checkout data case: " + caseName);
        }
        log.debug("Generate checkout data for case {}", name);
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        String postal = faker.address().zipCode();
        return switch (name) {
            case "valid" -> new CheckoutDetails(first, last, postal);
            case "hyphenated" -> new CheckoutDetails(first + "-" + faker.name().firstName(), last, postal);
            case "apostrophe" -> new CheckoutDetails(first, last + "'" + faker.name().lastName(), postal);
            case "leading_zero" -> new CheckoutDetails(first, last, faker.numerify("0####"));
            case "missing_first_name" -> new CheckoutDetails("", last, postal);
            case "missing_last_name" -> new CheckoutDetails(first, "", postal);
            case "missing_postal_code" -> new CheckoutDetails(first, last, "");
            case "missing_first_and_last_name" -> new CheckoutDetails("", "", postal);
            case "missing_first_name_and_postal_code" -> new CheckoutDetails("", last, "");
            case "missing_last_name_and_postal_code" -> new CheckoutDetails(first, "", "");
            case "empty" -> new CheckoutDetails("", "", "");
            default -> throw new IllegalStateException("Unhandled checkout case: " + name);
        };
    }

    private LoginAccount standard() {
        return LoginAccount.fromAlias("standard");
    }

    private String unknownUsername() {
        List<String> accepted = java.util.Arrays.stream(saucedemo.com.enums.User.values())
                .map(saucedemo.com.enums.User::getStandardUser).toList();
        String value;
        do {
            value = "invalid_" + faker.number().digits(12);
        } while (accepted.contains(value));
        return value;
    }

    private String incorrectPassword() {
        String value;
        do {
            value = faker.internet().password();
        } while (value.equals(standard().password()));
        return value;
    }
}

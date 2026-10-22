package saucedemo.com.helper;

import saucedemo.com.models.CheckoutDetails;
import saucedemo.com.models.LoginAccount;

/**
 * Typed fixture values used by Cucumber steps, without enum conversion.
 */
public final class FixtureData {
    private FixtureData() {
    }

    public record Login(String caseId, String username, String password,
                        String expectedPage, String expectedError) {
        public LoginAccount account() {
            return new LoginAccount(username, password);
        }

        @Override
        public String toString() {
            return "Login[caseId=" + caseId + ", username=" + username + ", password=<redacted>]";
        }
    }

    public record Product(String caseId, int productId, String title, String slug, String price) {
        public Product {
            if (productId < 0 || title == null || title.isBlank() || slug == null || slug.isBlank()) {
                throw new IllegalArgumentException("Product fixture requires a non-negative ID, title and full slug");
            }
        }
    }

    public record Checkout(String caseId, String firstName, String lastName, String postalCode,
                           String expectedPage, String expectedError) {
        public CheckoutDetails details() {
            return new CheckoutDetails(firstName, lastName, postalCode);
        }
    }
}

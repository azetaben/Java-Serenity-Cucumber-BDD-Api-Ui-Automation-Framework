package saucedemo.com.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDataTest {
    @Test
    void parsesLoginFixturesFromJsonCsvAndXlsx() {
        FixtureData.Login json = TestData.load("login.json", "standard", FixtureData.Login.class);
        FixtureData.Login csv = TestData.load("data.csv", "standard", FixtureData.Login.class);
        FixtureData.Login xlsx = TestData.load("data.xlsx", "standard", FixtureData.Login.class);
        assertAll(
                () -> assertEquals("standard_user", json.username()),
                () -> assertEquals(json.username(), csv.username()),
                () -> assertEquals(json.username(), xlsx.username()),
                () -> assertEquals("inventory", json.expectedPage()));
    }

    @Test
    void parsesProductYamlAndPreservesCheckoutPostalCodesAsText() {
        FixtureData.Product product = TestData.load("productItem.yml", "backpack", FixtureData.Product.class);
        FixtureData.Checkout checkout = TestData.load("checkoutInfo.json", "leading_zero", FixtureData.Checkout.class);
        assertEquals("Sauce Labs Backpack", product.title());
        assertEquals("00123", checkout.postalCode());
    }

    @Test
    void reportsMissingAndInvalidFixtureCasesClearly() {
        assertThrows(IllegalArgumentException.class,
                () -> TestData.load("login.json", "not-a-case", FixtureData.Login.class));
        assertThrows(IllegalArgumentException.class,
                () -> TestData.load("../login.json", "standard", FixtureData.Login.class));
    }
}

package saucedemo.com.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class TestDataTest {
    @Test
    void parsesLoginFixturesFromJsonCsvAndXlsx() {
        FixtureData.Login json = TestData.load("login.json", "standard", FixtureData.Login.class);
        FixtureData.Login csv = TestData.load("data.csv", "standard", FixtureData.Login.class);
        FixtureData.Login xlsx = TestData.load("data.xlsx", "standard", FixtureData.Login.class);
        assertSoftly(softly -> {
            softly.assertThat(json.username()).isEqualTo("standard_user");
            softly.assertThat(csv.username()).isEqualTo(json.username());
            softly.assertThat(xlsx.username()).isEqualTo(json.username());
            softly.assertThat(json.expectedPage()).isEqualTo("inventory");
        });
    }

    @Test
    void parsesProductYamlAndPreservesCheckoutPostalCodesAsText() {
        FixtureData.Product product = TestData.load("productItem.yml", "backpack", FixtureData.Product.class);
        FixtureData.Checkout checkout = TestData.load("checkoutInfo.json", "leading_zero", FixtureData.Checkout.class);
        assertThat(product.title()).isEqualTo("Sauce Labs Backpack");
        assertThat(checkout.postalCode()).isEqualTo("00123");
    }

    @Test
    void reportsMissingAndInvalidFixtureCasesClearly() {
        assertThatThrownBy(() -> TestData.load("login.json", "not-a-case", FixtureData.Login.class)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TestData.load("../login.json", "standard", FixtureData.Login.class)).isInstanceOf(IllegalArgumentException.class);
    }
}

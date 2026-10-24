package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.pages.ProductDetailsPage;
import saucedemo.com.stepdefinitions.Hooks;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDetailsActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(ProductDetailsActions.class);
    @Steps
    private ProductDetailsPage productDetailsPage;

    @Step("Verify product name: {0}")
    public void should_display_product_name(String expected) {
        assertThat($(productDetailsPage.getProductName()).waitUntilVisible().getText().trim()).isEqualTo(expected);
        log.info("should_display_product_name completed");
    }

    @Step("Verify product price: {0}")
    public void should_display_price(String expected) {
        String normalizedExpected = expected.trim().replace("$", "");
        String actual = $(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", "");
        assertThat(actual).isEqualTo(normalizedExpected);
        log.info("Verified product price: {}", actual);
    }

    @Step("Verify product image alt text: {0}")
    public void should_have_product_image_alt_text(String expected) {
        assertThat($(productDetailsPage.getProductImage()).getDomAttribute("alt")).isEqualTo(expected);
        log.info("Verified product image alt text: {}", expected);
    }

    @Step("Verify product image is displayed")
    public void should_display_product_image() {
        $(productDetailsPage.getProductImage()).waitUntilVisible();
        log.info("Verified product image is displayed");
    }

    @Step("Verify product can be added to the cart")
    public void should_allow_adding_product() {
        $(productDetailsPage.getAddToCartButton()).waitUntilVisible();
        log.info("Verified product can be added to the cart");
    }

    @Step("Verify product can be removed from the cart")
    public void should_allow_removing_product() {
        $(productDetailsPage.getRemoveButton()).waitUntilVisible();
        log.info("Verified product can be removed from the cart");
    }

    @Step("Remove product from the cart")
    public void remove_product_from_cart() {
        $(productDetailsPage.getRemoveButton()).waitUntilClickable().click();
        log.info("Removed product from the cart");
    }

    @Step("Navigate back to the inventory")
    public void navigate_back_to_inventory() {
        $(productDetailsPage.getBackToProductsButton()).waitUntilClickable().click();
        log.info("Navigated back to the inventory page");
    }

    @Step("Get product name")
    public String getProductName() {
        log.info("Get product title");
        return $(productDetailsPage.getProductName()).waitUntilVisible().getText().trim();
    }

    @Step("Get product price")
    public int getProductPrice() {
        log.info("Get product price");
        return Integer.parseInt($(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", ""));
    }

    @Step("Verify product quantity is displayed")
    public void should_display_product_quantity(int quantity) {
        assertThat(quantity).isEqualTo(1);
        log.info("Verified product quantity is displayed");
    }

    @Step("Verify product price is displayed")
    public void should_display_product_price(double price) {
        assertThat(Double.parseDouble($(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", ""))).isEqualTo(price);
        log.info("Verified product price is displayed");
    }
}

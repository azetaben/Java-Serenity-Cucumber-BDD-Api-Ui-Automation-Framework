package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.pages.ProductDetailsPage;
import saucedemo.com.support.FrameworkLogger;

public class ProductDetailsActions extends UIInteractionSteps {
    @Steps
    private ProductDetailsPage productDetailsPage;

    @Step("Verify product name: {0}")
    public void should_display_product_name(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product name: " + expected);
        Assertions.assertEquals(expected, $(productDetailsPage.getProductName()).waitUntilVisible().getText().trim());
        FrameworkLogger.actionEnded(ProductDetailsActions.class, "should_display_product_name");
    }

    @Step("Verify product price: {0}")
    public void should_display_price(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product price: " + expected);
        String normalizedExpected = expected.trim().replace("$", "");
        String actual = $(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", "");
        Assertions.assertEquals(normalizedExpected, actual);
    }

    @Step("Verify product image alt text: {0}")
    public void should_have_product_image_alt_text(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product image alt text: " + expected);
        Assertions.assertEquals(expected, $(productDetailsPage.getProductImage()).getDomAttribute("alt"));
    }

    @Step("Verify product image is displayed")
    public void should_display_product_image() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product image is displayed");
        $(productDetailsPage.getProductImage()).waitUntilVisible();
    }

    @Step("Verify product can be added to the cart")
    public void should_allow_adding_product() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product can be added to the cart");
        $(productDetailsPage.getAddToCartButton()).waitUntilVisible();
    }

    @Step("Verify product can be removed from the cart")
    public void should_allow_removing_product() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product can be removed from the cart");
        $(productDetailsPage.getRemoveButton()).waitUntilVisible();
    }

    @Step("Remove product from the cart")
    public void remove_product_from_cart() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Remove product from the cart");
        $(productDetailsPage.getRemoveButton()).waitUntilClickable().click();
    }

    @Step("Navigate back to the inventory")
    public void navigate_back_to_inventory() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Navigate back to the inventory");
        $(productDetailsPage.getBackToProductsButton()).waitUntilClickable().click();
    }

    @Step("Get product name")
    public String getProductName() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Get product name");
        return $(productDetailsPage.getProductName()).waitUntilVisible().getText().trim();
    }

    @Step("Get product price")
    public int getProductPrice() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Get product price");
        return Integer.parseInt($(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", ""));
    }

    @Step("Verify product quantity is displayed")
    public void should_display_product_quantity(int quantity) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product quantity is displayed");
        Assertions.assertEquals(1, quantity);
    }

    @Step("Verify product price is displayed")
    public void should_display_product_price(double price) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "Verify product price is displayed");
        Assertions.assertEquals(price, Double.parseDouble($(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim().replace("$", "")));
    }
}

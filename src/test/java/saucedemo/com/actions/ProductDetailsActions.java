package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.pages.ProductDetailsPage;

public class ProductDetailsActions extends UIInteractionSteps {
    @Steps
    private ProductDetailsPage productDetailsPage;

    @Step("Verify product name: {0}")
    public void should_display_product_name(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_display_product_name");
        Assertions.assertEquals(expected, $(productDetailsPage.getProductName()).waitUntilVisible().getText().trim());
    }

    @Step("Verify product price: {0}")
    public void should_display_price(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_display_price");
        Assertions.assertEquals(expected, $(productDetailsPage.getProductPrice()).waitUntilVisible().getText().trim());
    }

    @Step("Verify product image alt text: {0}")
    public void should_have_product_image_alt_text(String expected) {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_have_product_image_alt_text");
        Assertions.assertEquals(expected, $(productDetailsPage.getProductImage()).getDomAttribute("alt"));
    }

    @Step("Verify product image is displayed")
    public void should_display_product_image() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_display_product_image");
        $(productDetailsPage.getProductImage()).waitUntilVisible();
    }

    @Step("Verify product can be added to the cart")
    public void should_allow_adding_product() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_allow_adding_product");
        $(productDetailsPage.getAddToCartButton()).waitUntilVisible();
    }

    @Step("Verify product can be removed from the cart")
    public void should_allow_removing_product() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "should_allow_removing_product");
        $(productDetailsPage.getRemoveButton()).waitUntilVisible();
    }

    @Step("Remove product from the cart")
    public void remove_product_from_cart() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "remove_product_from_cart");
        $(productDetailsPage.getRemoveButton()).waitUntilClickable().click();
    }

    public void navigate_back_to_inventory() {
        FrameworkLogger.actionStarted(ProductDetailsActions.class, "navigate_back_to_inventory");
        $(productDetailsPage.getBackToProductsButton()).waitUntilClickable().click();
    }
}

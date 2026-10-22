package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.assertions.CollectionAssertions;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.CartPage;

import java.util.List;

public class CartActions extends UIInteractionSteps {
    @Steps
    private CartPage cartPage;

    @Step("Should contain item count: {0}")
    public void should_contain_item_count(int count) {
        FrameworkLogger.actionStarted(CartActions.class, "should_contain_item_count");
        Assertions.assertEquals(count, cartPage.items().count());
    }

    @Step("Should contain product: {0}")
    public void should_contain_product(Product p) {
        FrameworkLogger.actionStarted(CartActions.class, "should_contain_product");
        Assertions.assertTrue(cartPage.items().names().contains(p.getTitle()));
    }

    @Step("Should have titles: {0}")
    public void shouldHaveTitles(List<String> titles) {
        FrameworkLogger.actionStarted(CartActions.class, "shouldHaveTitles");
        CollectionAssertions.sameTitles(titles, cartPage.items().names());
    }

    @Step("Should have price for product: {0} as {1}")
    public void should_have_price(Product product, String price) {
        FrameworkLogger.actionStarted(CartActions.class, "should_have_price");
        Assertions.assertEquals(price, cartPage.items().displayedPrice(product.getId()));
    }

    @Step("Should have quantity for product: {0} as {1}")
    public void should_have_quantity(Product product, int quantity) {
        FrameworkLogger.actionStarted(CartActions.class, "should_have_quantity");
        Assertions.assertEquals(quantity, cartPage.items().read(product.getId()).quantity());
    }

    @Step("Should allow removal of product: {0}")
    public void should_allow_removal(Product product) {
        FrameworkLogger.actionStarted(CartActions.class, "should_allow_removal");
        $(cartPage.removeButtonFor(product.getSlug())).waitUntilVisible();
    }

    @Step("Should allow continuing shopping")
    public void should_allow_continue_shopping() {
        FrameworkLogger.actionStarted(CartActions.class, "should_allow_continue_shopping");
        Assertions.assertTrue($(cartPage.getContinueShoppingButton()).waitUntilVisible().isEnabled());
    }

    @Step("Should allow checkout")
    public void should_allow_checkout() {
        FrameworkLogger.actionStarted(CartActions.class, "should_allow_checkout");
        Assertions.assertTrue($(cartPage.getCheckoutButton()).waitUntilVisible().isEnabled());
    }

    @Step("Continue shopping")
    public void continue_shopping() {
        FrameworkLogger.actionStarted(CartActions.class, "continue_shopping");
        $(cartPage.getContinueShoppingButton()).waitUntilClickable().click();
    }

    @Step("Checkout")
    public void checkout() {
        FrameworkLogger.actionStarted(CartActions.class, "checkout");
        $(cartPage.getCheckoutButton()).waitUntilClickable().click();
    }
}

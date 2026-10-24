package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.assertions.CollectionAssertions;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.CartPage;
import saucedemo.com.pages.components.OrderItems;
import saucedemo.com.support.FrameworkLogger;

import java.math.BigDecimal;
import java.util.List;

public class CartActions extends UIInteractionSteps {
    @Steps
    private CartPage cartPage;

    public OrderItems items() {
        return new OrderItems($(cartPage.getCartContainer()));

    }


    @Step("Should contain item count: {0}")
    public void should_contain_item_count(int count) {
        FrameworkLogger.actionStarted(CartActions.class, "should_contain_item_count");
        Assertions.assertEquals(count, items().count());
    }

    @Step("Should contain product: {0}")
    public void should_contain_product(Product p) {
        FrameworkLogger.actionStarted(CartActions.class, "should_contain_product");
        Assertions.assertTrue(items().names().contains(p.getTitle()));
    }

    @Step("Should have titles: {0}")
    public void shouldHaveTitles(List<String> titles) {
        FrameworkLogger.actionStarted(CartActions.class, "shouldHaveTitles");
        CollectionAssertions.sameTitles(titles, items().names());
    }

    @Step("Should have price for product: {0} as {1}")
    public void should_have_price(Product product, String price) {
        FrameworkLogger.actionStarted(CartActions.class, "should_have_price");
        Assertions.assertEquals(price, items().displayedPrice(product.getId()));
    }

    @Step("Should display cart details for {0}: quantity {1}, price {2}")
    public void should_have_product_details(String productName, int quantity, String price) {
        FrameworkLogger.actionStarted(CartActions.class, "should_have_product_details");
        var item = items().read(productName);
        Assertions.assertEquals(quantity, item.quantity(), "Unexpected quantity for " + productName);
        BigDecimal expectedPrice = MoneyAssertions.parseDollarAmount(price);
        MoneyAssertions.assertAmountsEqual("Unexpected price for " + productName, expectedPrice, item.unitPrice());
    }

    @Step("Should have quantity {1} for cart product {0}")
    public void should_have_product_title_quantity(String productName, int quantity) {
        FrameworkLogger.actionStarted(CartActions.class, "should have quantity for cart product");
        Assertions.assertEquals(quantity, items().read(productName).quantity(),
                "Unexpected quantity for " + productName);
    }

    @Step("Should have quantity for product: {0} as {1}")
    public void should_have_quantity(Product product, int quantity) {
        FrameworkLogger.actionStarted(CartActions.class, "should have quantity for product");
        Assertions.assertEquals(quantity, items().read(product.getId()).quantity());
    }

    @Step("Should allow removal of product: {0}")
    public void should_allow_removal(Product product) {
        FrameworkLogger.actionStarted(CartActions.class, "should allow removal of product");
        $(cartPage.removeButtonFor(product.getSlug())).waitUntilVisible();
    }

    @Step("Should allow continuing shopping")
    public void should_allow_continue_shopping() {
        FrameworkLogger.actionStarted(CartActions.class, "should allow continuing shopping");
        Assertions.assertTrue($(cartPage.getContinueShoppingButton()).waitUntilVisible().isEnabled());
    }

    @Step("Should allow checkout")
    public void should_allow_checkout() {
        FrameworkLogger.actionStarted(CartActions.class, "should allow checkout");
        Assertions.assertTrue($(cartPage.getCheckoutButton()).waitUntilVisible().isEnabled());
    }

    @Step("Continue shopping")
    public void continue_shopping() {
        FrameworkLogger.actionStarted(CartActions.class, "continue shopping");
        $(cartPage.getContinueShoppingButton()).waitUntilClickable().click();
    }

    @Step("Checkout")
    public void checkout() {
        FrameworkLogger.actionStarted(CartActions.class, "I checkout");
        $(cartPage.getCheckoutButton()).waitUntilClickable().click();
    }

    public void should_display_product_name(String productTitle) {
        FrameworkLogger.actionStarted(CartActions.class, "should display product name");
        Assertions.assertTrue(items().names().contains(productTitle));
    }

    public String getProductQuantity() {
        return $(cartPage.getProductQuantities()).waitUntilVisible().getText().trim();
    }

    public String getProductPrice() {
        return $(cartPage.getProductPrice()).waitUntilVisible().getText().trim();
    }


    public void should_display_product_quantity(int quantity) {
        FrameworkLogger.actionStarted(CartActions.class, "should display product quantity");
        //Assertions.assertTrue(cartPage.items().quantities().contains(quantity));
        Assertions.assertEquals(quantity, Double.parseDouble($(cartPage.getProductQuantities()).waitUntilVisible().getText().trim()));
    }

    public void should_display_product_price(double $Price) {
        FrameworkLogger.actionStarted(CartActions.class, "should display product price");
        Assertions.assertEquals($Price, Double.parseDouble($(cartPage.getProductPrice()).waitUntilVisible().getText().trim()));
    }
}

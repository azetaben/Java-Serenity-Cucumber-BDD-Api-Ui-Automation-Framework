package saucedemo.com.actions;

import java.math.BigDecimal;
import java.util.List;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.assertions.CollectionAssertions;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.CartPage;
import saucedemo.com.pages.components.OrderItems;

import static org.assertj.core.api.Assertions.assertThat;

public class CartActions extends UIInteractionSteps {

    private static final Logger log = LoggerFactory.getLogger(CartActions.class);
    @Steps
    private CartPage cartPage;

    public OrderItems items() {
        return new OrderItems($(cartPage.getCartContainer()));

    }

    @Step("Should contain item count: {0}")
    public void should_contain_item_count(int count) {
        assertThat(items().count()).isEqualTo(count);
        log.info("Cart contains {} items", count);
    }

    @Step("Should contain product: {0}")
    public void should_contain_product(Product p) {
        log.info("Checking if cart contains product: {}", p.getTitle());
        assertThat(items().names().contains(p.getTitle())).isTrue();
        log.info("Cart contains product: {}", p.getTitle());
    }

    @Step("Should have titles: {0}")
    public void shouldHaveTitles(List<String> titles) {
        log.info("Checking if cart has titles: {}", titles);
        CollectionAssertions.sameTitles(titles, items().names());
        log.info("Cart has titles: {}", titles);
    }

    @Step("Should have price for product: {0} as {1}")
    public void should_have_price(Product product, String price) {
        log.info("Checking if cart has price for product: {} as {}", product.getTitle(), price);
        assertThat(items().displayedPrice(product.getId())).isEqualTo(price);
        log.info("Cart has price for product: {} as {}", product.getTitle(), price);
    }

    @Step("Should display cart details for {0}: quantity {1}, price {2}")
    public void should_have_product_details(String productName, int quantity, String price) {
        log.info("Checking cart details for {}: quantity {}, price {}", productName, quantity, price);
        var item = items().read(productName);
        assertThat(item.quantity()).as("Unexpected quantity for " + productName).isEqualTo(quantity);
        BigDecimal expectedPrice = MoneyAssertions.parseDollarAmount(price);
        MoneyAssertions.assertAmountsEqual("Unexpected price for " + productName, expectedPrice, item.unitPrice());
        log.info("Cart has details for {}: quantity {}, price {}", productName, quantity, price);
    }

    @Step("Should have quantity {1} for cart product {0}")
    public void should_have_product_title_quantity(String productName, int quantity) {
        log.info("Checking if cart has quantity {} for product {}", quantity, productName);
        assertThat(items().read(productName).quantity()).as("Unexpected quantity for " + productName).isEqualTo(quantity);
        log.info("Cart has quantity {} for product {}", quantity, productName);
    }

    @Step("Should have quantity for product: {0} as {1}")
    public void should_have_quantity(Product product, int quantity) {
        log.info("Checking if cart has quantity {} for product {}", quantity, product.getTitle());
        assertThat(items().read(product.getId()).quantity()).isEqualTo(quantity);
        log.info("Cart has quantity {} for product {}", quantity, product.getTitle());
    }

    @Step("Should allow removal of product: {0}")
    public void should_allow_removal(Product product) {
        $(cartPage.removeButtonFor(product.getSlug())).waitUntilVisible();
        log.info("Cart allows removal of product: {}", product.getTitle());
    }

    @Step("Should allow continuing shopping")
    public void should_allow_continue_shopping() {
        assertThat($(cartPage.getContinueShoppingButton()).waitUntilVisible().isEnabled()).isTrue();
        log.info("Cart allows continuing shopping as: {}", $(cartPage.getContinueShoppingButton()).waitUntilVisible().getText().trim());
    }

    @Step("Should allow checkout")
    public void should_allow_checkout() {
        assertThat($(cartPage.getCheckoutButton()).waitUntilVisible().isEnabled()).isTrue();
        log.info("Cart allows checkout as: {}", $(cartPage.getCheckoutButton()).waitUntilVisible().getText().trim());
    }

    @Step("Continue shopping")
    public void continue_shopping() {
        log.info("Clicked on {} button", $(cartPage.getContinueShoppingButton()).waitUntilVisible().getText().trim());
        $(cartPage.getContinueShoppingButton()).waitUntilClickable().click();
    }

    @Step("Checkout")
    public void checkout() {
        log.info("Clicked on {} button", $(cartPage.getCheckoutButton()).waitUntilVisible().getText().trim());
        $(cartPage.getCheckoutButton()).waitUntilClickable().click();
    }

    public void should_display_product_name(String productTitle) {
        assertThat(items().names().contains(productTitle)).isTrue();
        log.info("Cart displays product name: {}", productTitle);
    }

    public String getProductQuantity() {
        String quantity = $(cartPage.getProductQuantities()).waitUntilVisible().getText().trim();
        log.info("Retrieved product quantity: {}", quantity);
        return quantity;
    }

    public String getProductPrice() {
        String price = $(cartPage.getProductPrice()).waitUntilVisible().getText().trim();
        log.info("Retrieved product price: {}", price);
        return price;
    }

    public void should_display_product_quantity(int quantity) {
        assertThat(Double.parseDouble($(cartPage.getProductQuantities()).waitUntilVisible().getText().trim())).isEqualTo(quantity);
        log.info("Cart displays product quantity: {}", quantity);
    }

    public void should_display_product_price(double $Price) {
        assertThat(Double.parseDouble($(cartPage.getProductPrice()).waitUntilVisible().getText().trim())).isEqualTo($Price);
        log.info("Cart displays product price: {}", $Price);
    }
}

package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.components.OrderItems;
import saucedemo.com.pages.components.ProductLocators;

public class CartPage extends PageObject {

    private final By CART_CONTAINER = By.id("cart_contents_container");
    private final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");
    private final By CHECKOUT_BUTTON = By.id("checkout");

    public By getCartContainer() {
        return CART_CONTAINER;
    }

    public By getContinueShoppingButton() {
        return CONTINUE_SHOPPING_BUTTON;
    }

    public By getCheckoutButton() {
        return CHECKOUT_BUTTON;
    }

    public By itemFor(Product product) {
        return productRowFor(product.getId());
    }

    public OrderItems items() {
        return new OrderItems($(CART_CONTAINER));
    }

    public By productRowFor(int productId) {
        return ProductLocators.row(productId);
    }

    public By removeButtonFor(String itemSlug) {
        return ProductLocators.removeButton(itemSlug);
    }
}

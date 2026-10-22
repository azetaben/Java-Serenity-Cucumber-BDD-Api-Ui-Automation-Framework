package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class ProductDetailsPage extends PageObject {

    private final By PRODUCT_DETAILS_CONTAINER = By.id("inventory_item_container");
    private final By PRODUCT_DETAILS = By.cssSelector("#inventory_item_container [data-test='inventory-item']");
    private final By PRODUCT_NAME = By.cssSelector("#inventory_item_container [data-test='inventory-item-name']");
    private final By PRODUCT_DESCRIPTION = By.cssSelector("#inventory_item_container [data-test='inventory-item-desc']");
    private final By PRODUCT_PRICE = By.cssSelector("#inventory_item_container [data-test='inventory-item-price']");
    private final By PRODUCT_IMAGE = By.cssSelector("#inventory_item_container img.inventory_details_img");
    private final By REMOVE_BUTTON = By.cssSelector("#inventory_item_container button[data-test='remove'], "
            + "#inventory_item_container button[id^='remove-']");
    private final By ADD_TO_CART_BUTTON = By.cssSelector("#inventory_item_container button[data-test='add-to-cart'], "
            + "#inventory_item_container button[id^='add-to-cart-']");
    private final By BACK_TO_PRODUCTS_BUTTON = By.id("back-to-products");

    public By getProductDetailsContainer() {
        return PRODUCT_DETAILS_CONTAINER;
    }

    public By getProductDetails() {
        return PRODUCT_DETAILS;
    }

    public By getProductName() {
        return PRODUCT_NAME;
    }

    public By getProductDescription() {
        return PRODUCT_DESCRIPTION;
    }

    public By getProductPrice() {
        return PRODUCT_PRICE;
    }

    public By getProductImage() {
        return PRODUCT_IMAGE;
    }

    public By getRemoveButton() {
        return REMOVE_BUTTON;
    }

    public By getAddToCartButton() {
        return ADD_TO_CART_BUTTON;
    }

    public By getBackToProductsButton() { return BACK_TO_PRODUCTS_BUTTON; }
}

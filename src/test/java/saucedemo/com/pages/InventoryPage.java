package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.pages.components.ProductLocators;
import saucedemo.com.support.FrameworkLogger;

import java.math.BigDecimal;
import java.util.List;


public class InventoryPage extends PageObject {

    private final By SORT_DROPDOWN = By.cssSelector("[data-test='product-sort-container']");
    private final By INVENTORY_CONTAINER = By.cssSelector("[data-test='inventory-container']");
    private final By INVENTORY_LIST = By.cssSelector("[data-test='inventory-list']");
    private final By INVENTORY_ITEMS = By.cssSelector("[data-test='inventory-list'] [data-test='inventory-item']");
    private final By PRODUCT_NAME = ProductLocators.NAME;
    private final By PRODUCT_PRICE = ProductLocators.PRICE;
    private final By PRODUCT_IMAGE = By.cssSelector("img.inventory_item_img");


    public By getSortDropdown() {
        return SORT_DROPDOWN;
    }

    public By getInventoryContainer() {
        return INVENTORY_CONTAINER;
    }

    public By getInventoryList() {
        return INVENTORY_LIST;
    }

    public By getInventoryItems() {
        return INVENTORY_ITEMS;
    }

    public By getProductName() {
        return PRODUCT_NAME;
    }


    public By getProductPrice() {
        return PRODUCT_PRICE;
    }

    public By getProductImage() {
        return PRODUCT_IMAGE;
    }

    public By addToCartButtonFor(String itemSlug) {
        return ProductLocators.addButton(itemSlug);
    }

    public By removeFromCartButtonFor(String itemSlug) {
        return ProductLocators.removeButton(itemSlug);
    }


    public By productButtonFor(String action, String itemSlug) {
        return switch (action) {
            case "add-to-cart" -> addToCartButtonFor(itemSlug);
            case "remove" -> removeFromCartButtonFor(itemSlug);
            default -> throw new IllegalArgumentException("Unsupported product button action: " + action);
        };
    }

    public By productRowFor(int productId) {
        return ProductLocators.row(productId);
    }

    public By productTitleFor(int productId) {
        return ProductLocators.title(productId);
    }

    public List<BigDecimal> prices() {
        FrameworkLogger.pageOperationStarted(InventoryPage.class, "prices");
        return findAll(PRODUCT_PRICE).stream()
                .map(element -> MoneyAssertions.parseDollarAmount(element.getText()))
                .toList();
    }
}

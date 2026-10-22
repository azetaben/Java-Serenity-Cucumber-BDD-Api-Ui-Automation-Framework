package saucedemo.com.pages.components;

import org.openqa.selenium.By;

/**
 * Shared catalog markup used by inventory, cart and checkout.
 */
public final class ProductLocators {
    public static final By ROWS = By.cssSelector("[data-test='inventory-item']");
    public static final By NAME = By.cssSelector("[data-test='inventory-item-name']");
    public static final By PRICE = By.cssSelector("[data-test='inventory-item-price']");
    public static final By QUANTITY = By.cssSelector("[data-test='item-quantity']");

    private ProductLocators() {
    }

    public static By title(int id) {
        return By.id(titleId(id));
    }

    public static By row(int id) {
        return By.xpath(".//*[@id='" + titleId(id) + "']/ancestor::div[@data-test='inventory-item'][1]");
    }

    public static By addButton(String slug) {
        return By.id("add-to-cart-" + requireSlug(slug));
    }

    public static By removeButton(String slug) {
        return By.id("remove-" + requireSlug(slug));
    }

    private static String titleId(int id) {
        if (id < 0) throw new IllegalArgumentException("Product ID must not be negative");
        return "item_" + id + "_title_link";
    }

    private static String requireSlug(String slug) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException("Product slug must not be blank");
        return slug.trim();
    }
}

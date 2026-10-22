package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import saucedemo.com.assertions.CollectionAssertions;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.enums.Product;
import saucedemo.com.models.ProductItem;
import saucedemo.com.pages.InventoryPage;
import saucedemo.com.pages.ProductDetailsPage;
import saucedemo.com.pages.components.ProductLocators;

import java.util.*;

public class InventoryActions extends UIInteractionSteps {
    @Steps
    private InventoryPage inventoryPage;
    @Steps
    private ProductDetailsPage productDetailsPage;

    @Step("Verify on inventory page")
    public void verify_on_inventory_page() {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_on_inventory_page");
        waitForCondition().until(driver -> driver.getCurrentUrl().endsWith("/inventory.html"));
        $(inventoryPage.getInventoryList()).waitUntilVisible();
    }

    @Step("Add to cart product item: {0}")
    public void add_to_cart(ProductItem product) {
        FrameworkLogger.actionStarted(InventoryActions.class, "add_to_cart");
        $(inventoryPage.addToCartButtonFor(product.slug())).waitUntilClickable().click();
    }

    @Step("Add to cart product: {0}")
    public void add_to_cart(Product product) {
        FrameworkLogger.actionStarted(InventoryActions.class, "add_to_cart");
        add_to_cart(new ProductItem(product.getId(), product.getTitle(), product.getSlug(), product.getPrice()));
    }

    @Step("Remove item from cart: {0}")
    public void remove_item_from_cart(String slug) {
        FrameworkLogger.actionStarted(InventoryActions.class, "remove_item_from_cart");
        $(inventoryPage.removeFromCartButtonFor(slug)).waitUntilClickable().click();
    }

    @Step("Verify remove button is displayed for item: {0}")
    public void should_display_remove_button(String slug) {
        FrameworkLogger.actionStarted(InventoryActions.class, "should_display_remove_button");
        $(inventoryPage.removeFromCartButtonFor(slug)).waitUntilVisible();
    }

    @Step("Verify add to cart button is displayed for product: {0}")
    public void should_allow_adding_to_cart(Product product) {
        FrameworkLogger.actionStarted(InventoryActions.class, "should_allow_adding_to_cart");
        $(inventoryPage.addToCartButtonFor(product.getSlug())).waitUntilVisible();
    }

    @Step("Verify the price for product: {0} is {1}")
    public void should_display_price(Product product, String expected) {
        FrameworkLogger.actionStarted(InventoryActions.class, "should_display_price");
        String actual = $(inventoryPage.productRowFor(product.getId())).findElement(ProductLocators.PRICE).getText();
        Assertions.assertEquals(MoneyAssertions.parseDollarAmount(expected), MoneyAssertions.parseDollarAmount(actual));
    }

    @Step("Verify the product image is displayed for product: {0}")
    public void should_display_product_image(Product product) {
        FrameworkLogger.actionStarted(InventoryActions.class, "should_display_product_image");
        Assertions.assertTrue($(inventoryPage.productRowFor(product.getId())).findElement(By.cssSelector("img")).isDisplayed());
    }

    @Step("Verify the product titles are: {0}")
    public void shouldHaveTitles(List<String> expected) {
        FrameworkLogger.actionStarted(InventoryActions.class, "shouldHaveTitles");
        List<String> actual = findAll(ProductLocators.NAME).stream().map(element -> element.getText().trim()).toList();
        CollectionAssertions.sameTitles(expected, actual);
    }

    @Step("Verify the inventory has {0} items")
    public void shouldHaveCount(int expected) {
        FrameworkLogger.actionStarted(InventoryActions.class, "shouldHaveCount");
        Assertions.assertEquals(expected, findAll(inventoryPage.getInventoryItems()).size());
    }

    @Step("Open product details for product: {0}")
    public void open_product_details_by_title(Product product) {
        FrameworkLogger.actionStarted(InventoryActions.class, "open_product_details_by_title");
        $(inventoryPage.productTitleFor(product.getId())).waitUntilClickable().click();
        $(productDetailsPage.getProductDetailsContainer()).waitUntilVisible();
    }

    @Step("Sort inventory by: {0}")
    public void sort_inventory_by(String value) {
        FrameworkLogger.actionStarted(InventoryActions.class, "sort_inventory_by");
        $(inventoryPage.getSortDropdown()).selectByVisibleText(sortLabel(value));
    }

    @Step("Verify inventory order by field: {0} and direction: {1}")
    public void verify_inventory_order(String field, String direction) {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_inventory_order");
        validateSortRequest(field, direction);
        waitForCondition().until(driver -> inventoryIsOrdered(field, direction));
        if (field.equalsIgnoreCase("price")) verify_price_order(direction);
        else verify_name_order(direction);
    }

    @Step("Verify inventory price order by direction: {0}")
    public void verify_price_order(String direction) {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_price_order");
        validateSortRequest("price", direction);
        waitForCondition().until(driver -> inventoryIsOrdered("price", direction));
        assertPriceOrder(direction);
    }

    private boolean inventoryIsOrdered(String field, String direction) {
        if (field.equalsIgnoreCase("price")) {
            List<java.math.BigDecimal> actual = inventoryPage.prices();
            if (actual.isEmpty()) return false;
            List<java.math.BigDecimal> expected = new ArrayList<>(actual);
            expected.sort(Comparator.naturalOrder());
            if (direction.equalsIgnoreCase("descending")) Collections.reverse(expected);
            return expected.equals(actual);
        }

        List<String> actual = inventoryNames();
        if (actual.isEmpty()) return false;
        List<String> expected = new ArrayList<>(actual);
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        if (direction.equalsIgnoreCase("descending")) Collections.reverse(expected);
        return expected.equals(actual);
    }

    private void verify_name_order(String direction) {
        List<String> actual = inventoryNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        if (direction.equalsIgnoreCase("descending")) Collections.reverse(expected);
        Assertions.assertEquals(expected, actual, "Inventory order for name");
    }

    private List<String> inventoryNames() {
        return findAll(ProductLocators.NAME).stream()
                .map(element -> element.getText().trim()).toList();
    }

    private void assertPriceOrder(String direction) {
        List<java.math.BigDecimal> actual = inventoryPage.prices();
        List<java.math.BigDecimal> expected = new ArrayList<>(actual);
        expected.sort(Comparator.naturalOrder());
        if (direction.equalsIgnoreCase("descending")) Collections.reverse(expected);
        Assertions.assertEquals(expected, actual, "Inventory price order");
    }

    private void validateSortRequest(String field, String direction) {
        if (!field.equalsIgnoreCase("name") && !field.equalsIgnoreCase("price")) {
            throw new IllegalArgumentException("Unsupported inventory sort field: " + field);
        }
        if (!direction.equalsIgnoreCase("ascending") && !direction.equalsIgnoreCase("descending")) {
            throw new IllegalArgumentException("Unsupported sort direction: " + direction);
        }
    }

    @Step("Verify the first item has title: {0} and price: {1}")
    public void verify_first_item(String title, String price) {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_first_item");
        verify_item_at_position("first", title, price);
    }

    @Step("Verify the item at position: {0} has title: {1} and price: {2}")
    public void verify_item_at_position(String position, String title, String price) {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_item_at_position");
        List<net.serenitybdd.core.pages.WebElementFacade> items = findAll(inventoryPage.getInventoryItems());
        String normalizedPosition = position.trim().toLowerCase(Locale.ROOT);
        int index;
        if (normalizedPosition.equals("first")) {
            index = 0;
        } else if (normalizedPosition.equals("last")) {
            index = items.size() - 1;
        } else {
            String digits = normalizedPosition.replaceAll("\\D", "");
            if (digits.isEmpty()) {
                throw new IllegalArgumentException("Unsupported inventory position: " + position);
            }
            index = Integer.parseInt(digits) - 1;
        }
        Assertions.assertTrue(index >= 0 && index < items.size(),
                "Inventory position '" + position + "' is outside the available item range (" + items.size() + ")");
        Assertions.assertEquals(title, items.get(index).findElement(ProductLocators.NAME).getText().trim());
        String actualPrice = items.get(index).findElement(ProductLocators.PRICE).getText();
        Assertions.assertEquals(MoneyAssertions.parseDollarAmount(price), MoneyAssertions.parseDollarAmount(actualPrice));
    }

    @Step("Sort and verify item with sort: {0}, position: {1}, title: {2}, and price: {3}")
    public void sort_and_verify_item(String sort, String position, String title, String price) {
        FrameworkLogger.actionStarted(InventoryActions.class, "sort_and_verify_item");
        sort_inventory_by(sort);
        verify_item_at_position(position, title, price);
    }

    @Step("Verify button for product: {0} with label: {1}")
    public void verify_button_for_product(String label, String slug, String title) {
        FrameworkLogger.actionStarted(InventoryActions.class, "verify_button_for_product");
        By button = label.toLowerCase(Locale.ROOT).contains("remove")
                ? inventoryPage.removeFromCartButtonFor(slug) : inventoryPage.addToCartButtonFor(slug);
        Assertions.assertEquals(label, $(button).waitUntilVisible().getText().trim());
    }

    private String sortLabel(String value) {
        String normalized = value.toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        return switch (normalized) {
            case "name ascending", "name a to z" -> "Name (A to Z)";
            case "name descending", "name z to a" -> "Name (Z to A)";
            case "price ascending", "price low to high" -> "Price (low to high)";
            case "price descending", "price high to low" -> "Price (high to low)";
            default -> value;
        };
    }
}

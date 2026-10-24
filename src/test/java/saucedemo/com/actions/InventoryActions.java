package saucedemo.com.actions;

import java.math.BigDecimal;
import java.util.*;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.assertions.CollectionAssertions;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.enums.Product;
import saucedemo.com.models.ProductItem;
import saucedemo.com.pages.components.ProductLocators;
import saucedemo.com.pages.InventoryPage;
import saucedemo.com.pages.ProductDetailsPage;

import static org.assertj.core.api.Assertions.assertThat;

public class InventoryActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(InventoryActions.class);
    @Steps
    private InventoryPage inventoryPage;
    @Steps
    private ProductDetailsPage productDetailsPage;

    @Step("Verify on inventory page")
    public void verify_on_inventory_page() {
        log.info("Verify on inventory page");
        waitForCondition().until(driver -> driver.getCurrentUrl().endsWith("/inventory.html"));
        $(inventoryPage.getInventoryList()).waitUntilVisible();
        assertThat($(inventoryPage.getInventoryList()).isVisible()).isTrue();
        log.info("Verified on inventory page");
    }

    @Step("Add to cart product item: {0}")
    public void add_to_cart(ProductItem product) {
        log.info("Add to cart product item: {}", product);
        $(inventoryPage.addToCartButtonFor(product.slug())).waitUntilClickable().click();
        log.info("Clicked on add to cart button for product item: {}", product);
    }

    @Step("Add to cart product: {0}")
    public void add_to_cart(Product product) {
        add_to_cart(new ProductItem(product.getId(), product.getTitle(), product.getSlug(), product.getPrice()));
        log.info("Clicked on add to cart button for product: {}", product);
    }

    @Step("Remove item from cart: {0}")
    public void remove_item_from_cart(String slug) {
        log.info("Remove item from cart: {}", slug);
        if (getDriver().getCurrentUrl().contains("/inventory-item.html")) {
            $(productDetailsPage.getRemoveButton()).waitUntilClickable().click();
            return;
        }
        $(inventoryPage.removeFromCartButtonFor(slug)).waitUntilClickable().click();
        log.info("Clicked on remove from cart button for item: {}", slug);
    }

    @Step("Verify remove button is displayed for item: {0}")
    public void should_display_remove_button(String slug) {
        log.info("Verify remove button is displayed for item: {}", slug);
        $(inventoryPage.removeFromCartButtonFor(slug)).waitUntilVisible();
        log.info("Remove button is displayed for item: {}", slug);
    }

    @Step("Verify add to cart button is displayed for product: {0}")
    public void should_allow_adding_to_cart(Product product) {
        log.info("Verify add to cart button is displayed for product: {}", product);
        $(inventoryPage.addToCartButtonFor(product.getSlug())).waitUntilVisible();
        log.info("Add to cart button is displayed for product: {}", product);
    }

    @Step("Verify the price for product: {0} is {1}")
    public void should_display_price(Product product, String expected) {
        log.info("Verify the price for product: {} is {}", product, expected);
        String actual = $(inventoryPage.productRowFor(product.getId())).findElement(ProductLocators.PRICE).getText();
        assertThat(MoneyAssertions.parseDollarAmount(actual)).isEqualTo(MoneyAssertions.parseDollarAmount(expected));
        log.info("Verified the price for product: {} is {}", product, expected);
    }

    @Step("Verify the product image is displayed for product: {0}")
    public void should_display_product_image(Product product) {
        log.info("Verify the product image is displayed for product: {}", product);
        assertThat($(inventoryPage.productRowFor(product.getId())).findElement(By.cssSelector("img")).isDisplayed()).isTrue();
        log.info("Verified the product image is displayed for product: {}", product);
    }

    @Step("Verify the product titles are: {0}")
    public void shouldHaveTitles(List<String> expected) {
        log.info("Verify the product titles are: {}", expected);
        List<String> actual = findAll(ProductLocators.NAME).stream().map(element -> element.getText().trim()).toList();
        CollectionAssertions.sameTitles(expected, actual);
        log.info("Verified the product titles are: {}", expected);
    }

    @Step("Verify the inventory has {0} items")
    public void shouldHaveCount(int expected) {
        log.info("Verify the inventory has {} items", expected);
        assertThat(findAll(inventoryPage.getInventoryItems()).size()).isEqualTo(expected);
        log.info("Verified the inventory has {} items", expected);
    }

    @Step("Open product details for product: {0}")
    public void open_product_details_by_title(Product product) {
        log.info("Open product details for product: {}", product);
        $(inventoryPage.productTitleFor(product.getId())).waitUntilClickable().click();
        $(productDetailsPage.getProductDetailsContainer()).waitUntilVisible();
        log.info("Opened product details for product: {}", product);
    }

    @Step("Sort inventory by: {0}")
    public void sort_inventory_by(String value) {
        log.info("Sort inventory by: {}", value);
        $(inventoryPage.getSortDropdown()).selectByVisibleText(sortLabel(value));
        log.info("Sorted inventory by: {}", value);
    }

    @Step("Verify inventory order by field: {0} and direction: {1}")
    public void verify_inventory_order(String field, String direction) {
        log.info("Verify inventory order by field: {} and direction: {}", field, direction);
        validateSortRequest(field, direction);
        waitForCondition().until(driver -> inventoryIsOrdered(field, direction));
        if (field.equalsIgnoreCase("price")) verify_price_order(direction);
        else verify_name_order(direction);
        log.info("Verified inventory order by field: {} and direction: {}", field, direction);
    }

    @Step("Verify inventory price order by direction: {0}")
    public void verify_price_order(String direction) {
        log.info("Verify inventory price order by direction: {}", direction);
        validateSortRequest("price", direction);
        waitForCondition().until(driver -> inventoryIsOrdered("price", direction));
        assertPriceOrder(direction);
        log.info("Verified inventory price order by direction: {}", direction);
    }

    public List<BigDecimal> prices() {
        log.info("prices");
        log.info("Retrieved product prices: {}", findAll(inventoryPage.getProductPrice()).stream()
                .map(element -> MoneyAssertions.parseDollarAmount(element.getText()))
                .toList());
        return findAll(inventoryPage.getProductPrice()).stream()
                .map(element -> MoneyAssertions.parseDollarAmount(element.getText()))
                .toList();


    }

    private boolean inventoryIsOrdered(String field, String direction) {
        if (field.equalsIgnoreCase("price")) {
            List<java.math.BigDecimal> actual = prices();
            log.info("Checking if inventory is ordered by price in {} order: {}", direction, actual);
            if (actual.isEmpty()) {
                log.info("Inventory is empty, cannot determine order by price");
                return false;
            }
            List<java.math.BigDecimal> expected = new ArrayList<>(actual);
            expected.sort(Comparator.naturalOrder());
            if (direction.equalsIgnoreCase("descending")) {
                Collections.reverse(expected);
            }
            return expected.equals(actual);
        }

        List<String> actual = inventoryNames();
        if (actual.isEmpty()) return false;
        List<String> expected = new ArrayList<>(actual);
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        if (direction.equalsIgnoreCase("descending")) {
            Collections.reverse(expected);
        }
        return expected.equals(actual);
    }

    private void verify_name_order(String direction) {
        List<String> actual = inventoryNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        if (direction.equalsIgnoreCase("descending")) {
            Collections.reverse(expected);
        }
        assertThat(actual).as("Inventory order for name").isEqualTo(expected);
        log.info("Verified inventory order for name in {} order: {}", direction, actual);
    }

    private List<String> inventoryNames() {
        return findAll(ProductLocators.NAME).stream()
                .map(element -> element.getText().trim()).toList();
    }

    private void assertPriceOrder(String direction) {
        List<java.math.BigDecimal> actual = prices();
        List<java.math.BigDecimal> expected = new ArrayList<>(actual);
        expected.sort(Comparator.naturalOrder());
        if (direction.equalsIgnoreCase("descending")) {
            Collections.reverse(expected);
        }
        assertThat(actual).as("Inventory price order").isEqualTo(expected);
        log.info("Verified inventory price order in {} order: {}", direction, actual);
    }

    private void validateSortRequest(String field, String direction) {
        if (!field.equalsIgnoreCase("name") && !field.equalsIgnoreCase("price")) {
            log.info("Unsupported sort field: {}", field);
            throw new IllegalArgumentException("Unsupported inventory sort field: " + field);
        }
        if (!direction.equalsIgnoreCase("ascending") && !direction.equalsIgnoreCase("descending")) {
            log.info("Unsupported sort direction: {}", direction);
            throw new IllegalArgumentException("Unsupported sort direction: " + direction);
        }
    }

    @Step("Verify the first item has title: {0} and price: {1}")
    public void verify_first_item(String title, String price) {
        log.info("Verify the first item has title: {} and price: {}", title, price);
        verify_item_at_position("first", title, price);
        log.info("Verified the first item has title: {} and price: {}", title, price);
    }

    @Step("Verify the item at position: {0} has title: {1} and price: {2}")
    public void verify_item_at_position(String position, String title, String price) {
        log.info("Verify the item at position: {} has title: {} and price: {}", position, title, price);
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
        assertThat(index >= 0 && index < items.size()).as("Inventory position '" + position + "' is outside the available item range (" + items.size() + ")").isTrue();
        assertThat(items.get(index).findElement(ProductLocators.NAME).getText().trim()).isEqualTo(title);
        String actualPrice = items.get(index).findElement(ProductLocators.PRICE).getText();
        assertThat(MoneyAssertions.parseDollarAmount(actualPrice)).isEqualTo(MoneyAssertions.parseDollarAmount(price));
    }

    @Step("Sort and verify item with sort: {0}, position: {1}, title: {2}, and price: {3}")
    public void sort_and_verify_item(String sort, String position, String title, String price) {
        log.info("Sort and verify item with sort: {}, position: {}, title: {}, and price: {}", sort, position, title, price);
        sort_inventory_by(sort);
        verify_item_at_position(position, title, price);
        log.info("Sorted and verified item with sort: {}, position: {}, title: {}, and price: {}", sort, position, title, price);
    }

    @Step("Verify button for product: {0} with label: {1}")
    public void verify_button_for_product(String label, String slug, String title) {
        log.info("Verify button for product: {} with label: {}", title, label);
        By button = label.toLowerCase(Locale.ROOT).contains("remove")
                ? inventoryPage.removeFromCartButtonFor(slug) : inventoryPage.addToCartButtonFor(slug);
        assertThat($(button).waitUntilVisible().getText().trim()).isEqualTo(label);
        log.info("Verified button for product: {} with label: {}", title, label);
    }

    private String sortLabel(String value) {
        String normalized = value.toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        log.info("Converted sort value: {} to sort label: {}", value, normalized);
        return switch (normalized) {
            case "name ascending", "name a to z" -> "Name (A to Z)";
            case "name descending", "name z to a" -> "Name (Z to A)";
            case "price ascending", "price low to high" -> "Price (low to high)";
            case "price descending", "price high to low" -> "Price (high to low)";
            default -> value;
        };
    }

}

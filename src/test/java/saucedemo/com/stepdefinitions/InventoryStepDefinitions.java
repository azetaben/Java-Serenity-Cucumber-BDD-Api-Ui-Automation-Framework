package saucedemo.com.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.InventoryActions;
import saucedemo.com.enums.Product;
import saucedemo.com.models.ProductItem;
import saucedemo.com.support.FrameworkLogger;

import java.util.List;

public class InventoryStepDefinitions {
    @Steps
    private InventoryActions inventoryActions;

    @When("I am on the inventory page")
    public void iAmOnTheInventoryPage() {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "iAmOnTheInventoryPage");
        inventoryActions.verify_on_inventory_page();
    }

    @When("I sort the inventory by {string}")
    public void iSortTheInventoryBy(String sort) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "iSortTheInventoryBy");
        inventoryActions.sort_inventory_by(sort);
    }

    @Then("I sort the inventory by {string} the {string} inventory item should be {string} priced at {string}")
    public void sort_the_inventory_by(String sort, String position, String title, String price) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "sort_the_inventory_by");
        inventoryActions.sort_and_verify_item(sort, position, title, price);
    }

    @Then("the {string} inventory item should be {string} priced at {string}")
    public void verify_inventory_item_at_position(String position, String title, String price) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "verify_inventory_item_at_position");
        inventoryActions.verify_item_at_position(position, title, price);
    }

    @Then("the first inventory item should be {string} priced at {string}")
    public void the_first_inventory_item_should_be_priced_at(String title, String price) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_first_inventory_item_should_be_priced_at");
        inventoryActions.verify_first_item(title, price);
    }

    @When("I add the item {string} to the cart")
    public void i_add_the_item_to_the_cart(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_add_the_item_to_the_cart");
        inventoryActions.add_to_cart(ProductItem.fromReference(title));

    }

    @When("I add product {string} and verify its button changes to Remove")
    public void i_add_product_and_verify_its_button_changes_to_remove(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_add_product_and_verify_its_button_changes_to_remove");
        inventoryActions.add_to_cart(ProductItem.fromReference(title));
        inventoryActions.should_display_remove_button(Product.from(title).getSlug());
    }

    @When("Remove button is displayed for {string}")
    public void remove_button_is_displayed_for(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "remove_button_is_displayed_for");
        inventoryActions.should_display_remove_button(Product.from(title).getSlug());
    }

    @When("I remove {string}")
    public void i_remove(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_remove");
        inventoryActions.remove_item_from_cart(Product.from(title).getSlug());
    }

    @When("I remove product {string} and verify its button changes to Add to cart")
    public void i_remove_product_and_verify_its_button_changes_to_add_to_cart(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_remove_product_and_verify_its_button_changes_to_add_to_cart");
        inventoryActions.remove_item_from_cart(Product.from(title).getSlug());
        inventoryActions.should_allow_adding_to_cart(Product.from(title));
    }

    @When("I open product {string}")
    public void i_open_product(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_open_product");
        inventoryActions.open_product_details_by_title(Product.from(title));
    }

    @Then("the inventory contains these product titles:")
    public void the_inventory_contains_these_product_titles(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_inventory_contains_these_product_titles");
        inventoryActions.shouldHaveTitles(table.asList());
    }

    @Then("the product title count is {int}")
    public void the_product_title_count_is(int count) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_product_title_count_is");
        inventoryActions.shouldHaveCount(count);
    }

    @Then("the price of product {string} is {string}")
    public void the_price_of_product_is(String title, String price) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_price_of_product_is");
        inventoryActions.should_display_price(Product.from(title), price);
    }

    @Then("the image for product {string} is visible")
    public void the_image_for_product_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_image_for_product_is_visible");
        inventoryActions.should_display_product_image(Product.from(title));
    }

    @Then("the Add to cart button for product {string} is visible")
    public void the_add_to_cart_button_for_product_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_add_to_cart_button_for_product_is_visible");
        inventoryActions.should_allow_adding_to_cart(Product.from(title));
    }

    @Then("the inventory should be ordered by {string} {string}")
    public void the_inventory_should_be_ordered_by(String field, String direction) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "the_inventory_should_be_ordered_by");
        inventoryActions.verify_inventory_order(field, direction);
    }

    @When("I add product {string} to the cart")
    public void i_add_product_to_the_cart(String item) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_add_product_to_the_cart");
        inventoryActions.add_to_cart(ProductItem.fromReference(item));
    }

    @When("I add product items to the cart:")
    public void i_add_product_items_to_the_cart(DataTable items) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_add_product_items_to_the_cart");
        var products = items.asList(String.class).stream().map(ProductItem::fromReference).toList();
        products.forEach(inventoryActions::add_to_cart);
    }

    @When("I add {string} items to the cart")
    public void i_add_items_to_the_cart(String itemSlug) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_add_items_to_the_cart");
        inventoryActions.add_to_cart(ProductItem.fromReference(itemSlug));

    }

    @And("I should see {string} button for {string}")
    public void i_should_see_button_for(String buttonText, String productTitle) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_should_see_button_for");
        String itemSlug = Product.from(productTitle).getSlug();
        inventoryActions.verify_button_for_product(buttonText, itemSlug, productTitle);
    }

    @When("I remove {string} items from the cart")
    public void i_remove_items_from_the_cart(String itemSlug) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "i_remove_items_from_the_cart");
        inventoryActions.remove_item_from_cart(ProductItem.fromReference(itemSlug).slug());

    }

    @Then("all inventory prices should be in {string} order")
    public void verify_all_prices(String direction) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "verify_all_prices");
        inventoryActions.verify_price_order(direction);

    }

    @When("I add multiple items to the cart:")
    public void iAddMultipleItemsToTheCart(List<String> items) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "iAddMultipleItemsToTheCart");
        for (String item : items) {
            inventoryActions.add_to_cart(ProductItem.fromReference(item));
        }
    }

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToThePage() {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "iShouldBeRedirectedToThePage");
        inventoryActions.verify_on_inventory_page();
    }

    @When("I add a {string} to the cart")
    public void iAddAToTheCart(String productTitle) {
        FrameworkLogger.stepDefinitionStarted(InventoryStepDefinitions.class, "iAddAToTheCart");
        inventoryActions.add_to_cart(ProductItem.fromReference(productTitle));
    }
}

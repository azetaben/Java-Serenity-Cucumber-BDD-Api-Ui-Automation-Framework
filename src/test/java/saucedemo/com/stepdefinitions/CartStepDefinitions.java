package saucedemo.com.stepdefinitions;

import saucedemo.com.support.FrameworkLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.CartActions;
import saucedemo.com.actions.UiWorkflowActions;
import saucedemo.com.enums.Product;

public class CartStepDefinitions {
    @Steps
    private CartActions cartActions;
    @Steps
    private UiWorkflowActions workflow;


    @Then("the cart contains {int} products")
    public void the_cart_contains_products(int count) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_contains_products");
        cartActions.should_contain_item_count(count);

    }

    @Then("the cart should be empty")
    public void the_cart_should_be_empty() {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_should_be_empty");
        cartActions.should_contain_item_count(0);

    }

    @Then("the cart should contain only {string}")
    public void the_cart_should_contain_only(String title) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_should_contain_only");
        cartActions.should_contain_item_count(1);
        cartActions.should_contain_product(Product.from(title));
    }

    @Then("I should see the product {string} listed")
    public void i_should_see_the_product_listed(String title) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "i_should_see_the_product_listed");
        cartActions.should_contain_product(Product.from(title));
    }

    @Then("these cart product titles are displayed with Remove buttons:")
    public void these_cart_product_titles_are_displayed_with_remove_buttons(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "these_cart_product_titles_are_displayed_with_remove_buttons");
        cartActions.shouldHaveTitles(table.asList());
    }

    @Then("the cart price of {string} is {string}")
    public void the_cart_price_of_is(String title, String price) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_price_of_is");
        cartActions.should_have_price(Product.from(title), price);
    }

    @Then("the cart quantity of {string} is {int}")
    public void the_cart_quantity_of_is(String title, int quantity) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_quantity_of_is");
        cartActions.should_have_quantity(Product.from(title), quantity);
    }

    @Then("the cart Remove button for {string} is visible")
    public void the_cart_remove_button_for_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "the_cart_remove_button_for_is_visible");
        cartActions.should_allow_removal(Product.from(title));
    }

    @Then("Continue Shopping is visible and enabled")
    public void continue_shopping_is_visible_and_enabled() {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "continue_shopping_is_visible_and_enabled");
        cartActions.should_allow_continue_shopping();
    }

    @Then("Checkout is visible and enabled")
    public void checkout_is_visible_and_enabled() {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "checkout_is_visible_and_enabled");
        cartActions.should_allow_checkout();
    }

    @When("I remove cart product {string} and verify the badge decreases")
    public void i_remove_cart_product_and_verify_the_badge_decreases(String title) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "i_remove_cart_product_and_verify_the_badge_decreases");
        workflow.remove_cart_product(title);
    }

    @When("I continue shopping")
    public void i_continue_shopping() {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "i_continue_shopping");
        cartActions.continue_shopping();
    }

    @When("I proceed to checkout")
    public void i_proceed_to_checkout() {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "i_proceed_to_checkout");
        cartActions.checkout();
    }

    @Then("I should see the following cart product details")
    public void i_should_see_the_following_cart_product_details(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(CartStepDefinitions.class, "i_should_see_the_following_cart_product_details");
        for (var row : table.asMaps(String.class, String.class)) {
            Product product = Product.from(row.get("name"));
            cartActions.should_contain_product(product);
            cartActions.should_have_quantity(product, Integer.parseInt(row.get("quantity")));
            cartActions.should_have_price(product, row.get("price"));
        }
    }
}

package saucedemo.com.stepdefinitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import saucedemo.com.support.FrameworkLogger;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.ProductDetailsActions;
import saucedemo.com.actions.UiWorkflowActions;

public class ProductDetailsStepDefinitions {
    @Steps
    private ProductDetailsActions productDetailsActions;
    @Steps
    private UiWorkflowActions workflow;


    @Then("the details product title is {string}")
    public void the_details_product_title_is(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "the_details_product_title_is");
        productDetailsActions.should_display_product_name(title);
    }

    @Then("the details price of {string} is {string}")
    public void the_details_price_of_is(String title, String price) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "the_details_price_of_is");
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.should_display_price(price);
    }

    @Then("the details image of {string} is visible")
    public void the_details_image_of_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "the_details_image_of_is_visible");
        productDetailsActions.should_have_product_image_alt_text(title);
        productDetailsActions.should_display_product_image();
    }

    @Then("the details Add to cart button for {string} is visible")
    public void the_details_add_to_cart_button_for_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "the_details_add_to_cart_button_for_is_visible");
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.should_allow_adding_product();
    }

    @Then("the details Remove button for {string} is visible")
    public void the_details_remove_button_for_is_visible(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "the_details_remove_button_for_is_visible");
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.should_allow_removing_product();
    }

    @When("I add details product {string} and verify the badge increases")
    public void i_add_details_product_and_verify_the_badge_increases(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "i_add_details_product_and_verify_the_badge_increases");
        productDetailsActions.should_display_product_name(title);
        workflow.add_product_to_details();
    }

    @When("I add the product to the shopping cart")
    public void i_add_the_product_to_the_shopping_cart() {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "i_add_the_product_to_the_shopping_cart");
        workflow.add_product_to_details();
    }

    @When("I remove details product {string}")
    public void i_remove_details_product(String title) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "i_remove_details_product");
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.remove_product_from_cart();
    }

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "iAddToTheCart");
        productDetailsActions.should_display_product_name(productName);
        workflow.add_product_to_details();
    }


    @And("I remove {string} item from the cart")
    public void iRemoveItemFromTheCart(String arg0) {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "iRemoveItemFromTheCart");
        productDetailsActions.remove_product_from_cart();
    }

    @When("I tap on the back to products button")
    public void iTapOnTheBackToProductsButton() {
        FrameworkLogger.stepDefinitionStarted(ProductDetailsStepDefinitions.class, "iTapOnTheBackToProductsButton");
        productDetailsActions.navigate_back_to_inventory();
    }
}

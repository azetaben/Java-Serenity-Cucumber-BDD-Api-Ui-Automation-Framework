package saucedemo.com.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.ProductDetailsActions;
import saucedemo.com.actions.UiWorkflowActions;

import java.util.Map;

public class ProductDetailsStepDefinitions {
    @Steps
    private ProductDetailsActions productDetailsActions;
    @Steps
    private UiWorkflowActions workflow;


    @Then("the product title should be {string}")
    public void the_product_title_should_be(String title) {
        productDetailsActions.should_display_product_name(title);
    }

    @Then("the price for {string} should be {string}")
    public void the_price_for_should_be(String title, String price) {
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.should_display_price(price);
    }

    @Then("I can see the following cart product details")
    public void i_can_see_the_following_cart_product_details(DataTable table) {
        for (var row : table.asMaps(String.class, String.class)) {
            productDetailsActions.should_display_product_name(row.get("Product Name").trim());
            productDetailsActions.should_display_price(row.get("Price").trim());

        }
    }

    @Then("the product image for {string} should be visible")
    public void the_product_image_for_should_be_visible(String title) {
        productDetailsActions.should_have_product_image_alt_text(title);
        productDetailsActions.should_display_product_image();
    }

    @Then("the Add to cart button for {string} should be visible")
    public void the_add_to_cart_button_for_should_be_visible(String title) {
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.should_allow_adding_product();
    }

    @Then("the Remove button for {string} should be visible")
    public void the_remove_button_for_should_be_visible(String title) {
        productDetailsActions.should_display_product_name(title);
    }

    @When("I add details product {string} and verify the badge increases")
    public void i_add_details_product_and_verify_the_badge_increases(String title) {
        productDetailsActions.should_display_product_name(title);
        workflow.add_product_to_the_cart();
    }

    @When("I add the product to the shopping cart")
    public void i_add_the_product_to_the_shopping_cart() {
        workflow.add_product_to_the_cart();
    }

    @When("I remove {string} from the cart")
    public void i_remove_details_product(String title) {
        productDetailsActions.should_display_product_name(title);
        productDetailsActions.remove_product_from_cart();
    }

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        productDetailsActions.should_display_product_name(productName);
        workflow.add_product_to_the_cart();
    }


    @And("I remove {string} item from the cart")
    public void iRemoveItemFromTheCart(String arg0) {
        productDetailsActions.remove_product_from_cart();
    }

    @When("I tap on the back to products button")
    public void iTapOnTheBackToProductsButton() {
        productDetailsActions.navigate_back_to_inventory();
    }

    @And("I should see the following item(s) details:")
    public void iShouldSeeTheFollowingItemsDetails(DataTable table) {
        Map<String, String> details = table.transpose().asMap(String.class, String.class);
        productDetailsActions.should_display_product_name(details.get("Product Name").trim());
        productDetailsActions.should_display_price((details.get("Price").trim()));
    }

    @Then("I should see {string} item in the cart")
    public void iShouldSeeInTheCart(String productName, DataTable table) {
        Map<String, String> details = table.transpose().asMap(String.class, String.class);
        productDetailsActions.should_display_product_name(productName);
        productDetailsActions.should_display_product_name(details.get("Product Name").trim());
        productDetailsActions.should_display_product_price(
                Double.parseDouble(details.get("$Price").trim().replace("$", "")));
    }


    @Then("I should see item details in the cart")
    public void iShouldSeeItemDetailsInTheCart(DataTable table) {
        Map<String, String> item_details = table.transpose().asMap(String.class, String.class);
        productDetailsActions.should_display_product_name(item_details.get("Product Title"));
        productDetailsActions.should_display_product_price(Double.parseDouble(item_details.get("$Price")));
        productDetailsActions.should_display_product_quantity(Integer.parseInt(item_details.get("Quantity")));
    }

}

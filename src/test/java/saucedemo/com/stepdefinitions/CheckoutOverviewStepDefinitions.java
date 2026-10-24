package saucedemo.com.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.CheckoutOverviewActions;

public class CheckoutOverviewStepDefinitions {
    @Steps
    private CheckoutOverviewActions checkoutOverviewActions;


    @Then("the order summary should contain exactly these products:")
    public void the_order_summary_should_contain_exactly_these_products(DataTable table) {
        checkoutOverviewActions.shouldContainProducts(table.asMaps());
    }

    @Then("the item subtotal should be {string}")
    public void the_item_subtotal_should_be(String amount) {
        checkoutOverviewActions.should_have_subtotal(new java.math.BigDecimal(amount));
    }

    @Then("the item subtotal should equal the sum of product prices multiplied by quantities")
    public void the_item_subtotal_should_equal_the_sum_of_product_prices_multiplied_by_quantities() {
        checkoutOverviewActions.should_have_subtotal_matching_items();
    }

    @Then("the displayed tax should equal the configured tax rate applied to the item subtotal")
    public void the_displayed_tax_should_equal_the_configured_tax_rate_applied_to_the_item_subtotal() {
        checkoutOverviewActions.should_have_tax_at_configured_rate();
    }

    @Then("the displayed total should equal the item subtotal plus tax")
    public void the_displayed_total_should_equal_the_item_subtotal_plus_tax() {
        checkoutOverviewActions.should_have_consistent_total();
    }

    @When("I finish checkout")
    public void i_finish_checkout() {
        checkoutOverviewActions.finish_checkout();
    }
}

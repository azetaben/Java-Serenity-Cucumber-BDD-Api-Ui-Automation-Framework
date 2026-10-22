package saucedemo.com.stepdefinitions;

import saucedemo.com.support.FrameworkLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.CheckoutInfoActions;
import saucedemo.com.actions.UiWorkflowActions;
import saucedemo.com.models.CheckoutDetails;

public class CheckoutInfoStepDefinitions {
    @Steps
    private CheckoutInfoActions checkoutInfoActions;
    @Steps
    private UiWorkflowActions workflow;

    @ParameterType("[A-Za-z0-9_ -]+")
    public saucedemo.com.enums.CheckoutInfo checkoutInfo(String value) {
        return saucedemo.com.enums.CheckoutInfo.from(value);
    }

    @Then("I should see the checkout information page")
    public void i_should_see_the_checkout_information_page() {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_should_see_the_checkout_information_page");
        checkoutInfoActions.should_display_checkout_information_form();
    }

    @When("I enter checkout information first name {string}, last name {string}, and postal code {string}")
    public void i_enter_checkout_information_first_name_last_name_and_postal_code(String first, String last, String postal) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_enter_checkout_information_first_name_last_name_and_postal_code");
        checkoutInfoActions.enter_checkout_information(first, last, postal);
    }

    @When("I enter the following customer details:")
    public void i_enter_the_following_customer_details(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_enter_the_following_customer_details");
        checkoutInfoActions.enter_checkout_information(CheckoutDetails.fromRow(table.asMap(String.class, String.class)));
    }

    @When("I enter checkout information below:")
    public void i_enter_checkout_information_below(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_enter_checkout_information_below");
        var rows = table.asMaps(String.class, String.class);
        if (rows.size() != 1) {
            throw new IllegalArgumentException("Checkout table must contain exactly one customer row");
        }
        checkoutInfoActions.enter_checkout_information(CheckoutDetails.fromRow(rows.get(0)));
    }

    @When("I continue from checkout information")
    public void i_continue_from_checkout_information() {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_continue_from_checkout_information");
        checkoutInfoActions.click_continue();
    }

    @When("I submit checkout information")
    public void i_submit_checkout_information() {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_submit_checkout_information");
        workflow.submit_default_customer_details();
    }

    @Then("I should see checkout information error {string}")
    public void i_should_see_checkout_information_error(String error) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_should_see_checkout_information_error");
        checkoutInfoActions.should_display_error(error);
    }


    @When("I submit checkout information using \"{checkoutInfo}\"")
    public void i_submit_checkout_information_using(saucedemo.com.enums.CheckoutInfo data) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "i_submit_checkout_information_using");
        checkoutInfoActions.complete_checkout_information(data);
    }

    @Then("the checkout information result should match \"{checkoutInfo}\"")
    public void the_checkout_information_result_should_match(saucedemo.com.enums.CheckoutInfo data) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "the_checkout_information_result_should_match");
        workflow.verify_page(data.getExpectedPage());
        data.getExpectedError().ifPresent(checkoutInfoActions::should_display_error);
    }

    @Then("the checkout validation should require {string}")
    public void checkout_validation_should_require(String field) {
        FrameworkLogger.stepDefinitionStarted(CheckoutInfoStepDefinitions.class, "checkout_validation_should_require");
        switch (field.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "first name" -> checkoutInfoActions.should_require_first_name();
            case "last name" -> checkoutInfoActions.should_require_last_name();
            case "postal code" -> checkoutInfoActions.should_require_postal_code();
            default -> throw new IllegalArgumentException("Unsupported required checkout field: " + field);
        }
    }
}

package saucedemo.com.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.CheckoutCompleteActions;
import saucedemo.com.actions.OrderDocumentActions;

public class CheckoutCompleteStepDefinitions {
    @Steps
    private CheckoutCompleteActions checkoutCompleteActions;
    @Steps
    private OrderDocumentActions orderDocumentActions;


    @Then("I should see the checkout confirmation {string}")
    public void i_should_see_the_checkout_confirmation(String heading) {
        checkoutCompleteActions.should_display_confirmation_heading(heading);
    }

    @Then("I should see the order completion message:")
    public void i_should_see_the_order_completion_message(String message) {
        checkoutCompleteActions.should_display_confirmation_message(message);
    }

    @Then("the Pony Express icon is visible")
    public void the_pony_express_icon_is_visible() {
        checkoutCompleteActions.should_display_pony_express_image();
    }

    @Then("the order dispatch message is visible")
    public void the_order_dispatch_message_is_visible() {
        checkoutCompleteActions.should_confirm_order_completed();
    }

    @Then("Generate PDF order and Back Home are displayed")
    public void generate_pdf_order_and_back_home_are_displayed() {
        checkoutCompleteActions.should_allow_generating_pdf_order();
        checkoutCompleteActions.should_allow_returning_home();
    }

    @Then("the Back Home button is displayed")
    public void the_back_home_button_is_displayed() {
        checkoutCompleteActions.should_allow_returning_home();
    }

    @When("I generate and download the order PDF into downloadfiles")
    public void i_generate_and_download_the_order_pdf_into_downloadfiles() {
        orderDocumentActions.download_order_pdf();
    }

    @Then("the downloaded order PDF should contain {string}")
    public void the_downloaded_order_pdf_should_contain(String expected) {
        orderDocumentActions.verify_downloaded_pdf_contains(expected);
    }
}

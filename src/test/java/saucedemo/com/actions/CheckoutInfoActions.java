package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.enums.CheckoutInfo;
import saucedemo.com.models.CheckoutDetails;
import saucedemo.com.pages.CheckoutInfoPage;
import saucedemo.com.stepdefinitions.Hooks;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutInfoActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(CheckoutInfoActions.class);
    @Steps
    private CheckoutInfoPage checkoutInfoPage;

    @Step("Should display checkout information form")
    public void should_display_checkout_information_form() {
        $(checkoutInfoPage.getCheckoutForm()).waitUntilVisible();
        log.info("Checkout information form is displayed");
    }

    @Step("Enter checkout information: {0}, {1}, {2}")
    public void enter_checkout_information(String first, String last, String postal) {
        enter_checkout_information(new CheckoutDetails(first, last, postal));
        log.info("Entered checkout information: {}, {}, {}", first, last, postal);
    }

    @Step("Enter checkout information: {0}")
    public void enter_checkout_information(CheckoutDetails details) {
        $(checkoutInfoPage.getFirstNameInput()).waitUntilClickable().clear();
        $(checkoutInfoPage.getFirstNameInput()).type(details.firstName());
        $(checkoutInfoPage.getLastNameInput()).clear();
        $(checkoutInfoPage.getLastNameInput()).type(details.lastName());
        $(checkoutInfoPage.getPostalCodeInput()).clear();
        $(checkoutInfoPage.getPostalCodeInput()).type(details.postalCode());
        log.info("Entered checkout information: {}, {}, {}", details.firstName(), details.lastName(), details.postalCode());
    }

    @Step("Click continue button")
    public void click_continue() {
        $(checkoutInfoPage.getContinueButton()).waitUntilClickable().click();
        log.info("Clicked continue button");
    }

    @Step("Should display error message: {0}")
    public void should_display_error(String expected) {
        log.info("Should display error message: {}", expected);
        assertThat($(checkoutInfoPage.getErrorMessage()).waitUntilVisible().getText().trim()).isEqualTo(expected);
    }

    @Step("Should require first name")
    public void should_require_first_name() {
        should_display_error("Error: First Name is required");
        log.info("Verified first name is required");
    }

    @Step("Should require last name")
    public void should_require_last_name() {
        should_display_error("Error: Last Name is required");
        log.info("Verified last name is required");
    }

    @Step("Should require postal code")
    public void should_require_postal_code() {
        should_display_error("Error: Postal Code is required");
        log.info("Verified postal code is required");
    }

    @Step("Complete checkout information: {0}")
    public void complete_checkout_information(CheckoutInfo data) {
        enter_checkout_information(data.details());
        click_continue();
        log.info("Checkout information completed: first name {}, last name {}, postal code {}", data.details().firstName(), data.details().lastName(), data.details().postalCode());
    }
}

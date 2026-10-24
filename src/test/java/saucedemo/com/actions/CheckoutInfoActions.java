package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.enums.CheckoutInfo;
import saucedemo.com.models.CheckoutDetails;
import saucedemo.com.pages.CheckoutInfoPage;
import saucedemo.com.support.FrameworkLogger;

public class CheckoutInfoActions extends UIInteractionSteps {
    @Steps
    private CheckoutInfoPage checkoutInfoPage;

    @Step("Should display checkout information form")
    public void should_display_checkout_information_form() {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Should display checkout information form");
        $(checkoutInfoPage.getCheckoutForm()).waitUntilVisible();
    }

    @Step("Enter checkout information: {0}, {1}, {2}")
    public void enter_checkout_information(String first, String last, String postal) {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Enter checkout information");
        enter_checkout_information(new CheckoutDetails(first, last, postal));
    }

    @Step("Enter checkout information: {0}")
    public void enter_checkout_information(CheckoutDetails details) {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Enter checkout information");
        $(checkoutInfoPage.getFirstNameInput()).waitUntilClickable().clear();
        $(checkoutInfoPage.getFirstNameInput()).type(details.firstName());
        $(checkoutInfoPage.getLastNameInput()).clear();
        $(checkoutInfoPage.getLastNameInput()).type(details.lastName());
        $(checkoutInfoPage.getPostalCodeInput()).clear();
        $(checkoutInfoPage.getPostalCodeInput()).type(details.postalCode());
    }

    @Step("Click continue button")
    public void click_continue() {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Click continue button");
        $(checkoutInfoPage.getContinueButton()).waitUntilClickable().click();
    }

    @Step("Should display error message: {0}")
    public void should_display_error(String expected) {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Should display error message");
        Assertions.assertEquals(expected, $(checkoutInfoPage.getErrorMessage()).waitUntilVisible().getText().trim());
    }

    @Step("Should require first name")
    public void should_require_first_name() {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Should require first name");
        should_display_error("Error: First Name is required");
    }

    @Step("Should require last name")
    public void should_require_last_name() {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Should require last name");
        should_display_error("Error: Last Name is required");
    }

    @Step("Should require postal code")
    public void should_require_postal_code() {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Should require postal code");
        should_display_error("Error: Postal Code is required");
    }

    @Step("Complete checkout information: {0}")
    public void complete_checkout_information(CheckoutInfo data) {
        FrameworkLogger.actionStarted(CheckoutInfoActions.class, "Complete checkout information");
        enter_checkout_information(data.details());
        click_continue();
    }
}

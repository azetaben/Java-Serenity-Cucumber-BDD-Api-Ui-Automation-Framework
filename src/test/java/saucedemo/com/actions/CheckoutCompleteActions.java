package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.pages.CheckoutCompletePage;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutCompleteActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(CheckoutCompleteActions.class);
    @Steps
    private CheckoutCompletePage checkoutCompletePage;

    @Step("Should display confirmation heading: {0}")
    public void should_display_confirmation_heading(String expected) {
        assertThat($(checkoutCompletePage.getCompleteHeader()).waitUntilVisible().getText().trim()).isEqualTo(expected);
        log.info("Displayed confirmation heading: {}", expected);
    }

    @Step("Should display confirmation message: {0}")
    public void should_display_confirmation_message(String expected) {
        assertThat($(checkoutCompletePage.getCompleteText()).waitUntilVisible().getText().trim()).isEqualTo(expected.trim());
        log.info("Displayed confirmation message: {}", expected);
    }

    @Step("Should display pony express image")
    public void should_display_pony_express_image() {
        $(checkoutCompletePage.getPonyExpressImage()).waitUntilVisible();
        log.info("Displayed pony express image");
    }

    @Step("Should confirm order completed")
    public void should_confirm_order_completed() {
        $(checkoutCompletePage.getCompleteText()).waitUntilVisible();
        log.info("Confirmed order completed");
    }

    @Step("Should allow generating PDF order")
    public void should_allow_generating_pdf_order() {
        $(checkoutCompletePage.getGeneratePdfOrderButton()).waitUntilVisible();
        log.info("Allowed generating PDF order");
    }

    @Step("Should allow returning home")
    public void should_display_back_home_button() {
        $(checkoutCompletePage.getBackHomeButton()).waitUntilVisible();
        log.info("Displayed back home button");
    }

    @Step("Should allow returning home")
    public void should_allow_returning_home() {
        $(checkoutCompletePage.getBackHomeButton()).waitUntilVisible();
        log.info("Allowed returning home");
    }
}

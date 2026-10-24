package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.pages.CheckoutCompletePage;
import saucedemo.com.support.FrameworkLogger;

public class CheckoutCompleteActions extends UIInteractionSteps {
    @Steps
    private CheckoutCompletePage checkoutCompletePage;

    @Step("Should display confirmation heading: {0}")
    public void should_display_confirmation_heading(String expected) {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should display confirmation heading");
        Assertions.assertEquals(expected, $(checkoutCompletePage.getCompleteHeader()).waitUntilVisible().getText().trim());
    }

    @Step("Should display confirmation message: {0}")
    public void should_display_confirmation_message(String expected) {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should display confirmation message");
        Assertions.assertEquals(expected.trim(), $(checkoutCompletePage.getCompleteText()).waitUntilVisible().getText().trim());
    }

    @Step("Should display pony express image")
    public void should_display_pony_express_image() {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should display pony express image");
        $(checkoutCompletePage.getPonyExpressImage()).waitUntilVisible();
    }

    @Step("Should confirm order completed")
    public void should_confirm_order_completed() {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should confirm order completed");
        $(checkoutCompletePage.getCompleteText()).waitUntilVisible();
    }

    @Step("Should allow generating PDF order")
    public void should_allow_generating_pdf_order() {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should allow generating PDF order");
        $(checkoutCompletePage.getGeneratePdfOrderButton()).waitUntilVisible();
    }

    @Step("Should allow returning home")
    public void should_display_back_home_button() {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should display back home button");
        $(checkoutCompletePage.getBackHomeButton()).waitUntilVisible();
    }

    @Step("Should allow returning home")
    public void should_allow_returning_home() {
        FrameworkLogger.actionStarted(CheckoutCompleteActions.class, "Should allow returning home");
        $(checkoutCompletePage.getBackHomeButton()).waitUntilVisible();
    }
}

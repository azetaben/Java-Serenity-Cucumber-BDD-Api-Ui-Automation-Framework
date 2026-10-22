package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class CheckoutInfoPage extends PageObject {

    private final By CHECKOUT_INFO_CONTAINER = By.id("checkout_info_container");
    private final By CHECKOUT_FORM = By.cssSelector("#checkout_info_container form[aria-label='Checkout information']");
    private final By FIRST_NAME_INPUT = By.id("first-name");
    private final By LAST_NAME_INPUT = By.id("last-name");
    private final By POSTAL_CODE_INPUT = By.id("postal-code");
    private final By ERROR_MESSAGE = By.cssSelector("#checkout_info_container [data-test='error']");
    private final By DISMISS_ERROR_BUTTON = By.cssSelector("#checkout_info_container [data-test='error-button']");
    private final By CANCEL_BUTTON = By.id("cancel");
    private final By CONTINUE_BUTTON = By.id("continue");

    public By getCheckoutInfoContainer() {
        return CHECKOUT_INFO_CONTAINER;
    }

    public By getCheckoutForm() {
        return CHECKOUT_FORM;
    }

    public By getFirstNameInput() {
        return FIRST_NAME_INPUT;
    }

    public By getLastNameInput() {
        return LAST_NAME_INPUT;
    }

    public By getPostalCodeInput() {
        return POSTAL_CODE_INPUT;
    }

    public By getErrorMessage() {
        return ERROR_MESSAGE;
    }

    public By getDismissErrorButton() {
        return DISMISS_ERROR_BUTTON;
    }

    public By getCancelButton() {
        return CANCEL_BUTTON;
    }

    public By getContinueButton() {
        return CONTINUE_BUTTON;
    }

}

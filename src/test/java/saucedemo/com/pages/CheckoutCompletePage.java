package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class CheckoutCompletePage extends PageObject {

    private final By CHECKOUT_COMPLETE_CONTAINER = By.id("checkout_complete_container");
    private final By PONY_EXPRESS_IMAGE = By.cssSelector("#checkout_complete_container .pony_express");
    private final By COMPLETE_HEADER = By.cssSelector("#checkout_complete_container [data-test='complete-header']");
    private final By COMPLETE_TEXT = By.cssSelector("#checkout_complete_container [data-test='complete-text']");
    private final By BACK_HOME_BUTTON = By.id("back-to-products");
    private final By GENERATE_PDF_ORDER_BUTTON = By.id("generate-pdf-order");


    public By getCheckoutCompleteContainer() {
        return CHECKOUT_COMPLETE_CONTAINER;
    }

    public By getPonyExpressImage() {
        return PONY_EXPRESS_IMAGE;
    }

    public By getCompleteHeader() {
        return COMPLETE_HEADER;
    }

    public By getCompleteText() {
        return COMPLETE_TEXT;
    }

    public By getBackHomeButton() {
        return BACK_HOME_BUTTON;
    }

    public By getGeneratePdfOrderButton() {
        return GENERATE_PDF_ORDER_BUTTON;
    }

}

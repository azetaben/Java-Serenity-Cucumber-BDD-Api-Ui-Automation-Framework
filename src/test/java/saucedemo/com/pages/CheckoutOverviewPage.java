package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;

public class CheckoutOverviewPage extends PageObject {

    private final By CHECKOUT_SUMMARY_CONTAINER = By.id("checkout_summary_container");
    private final By CART_LIST = By.cssSelector("#checkout_summary_container [data-test='cart-list']");
    private final By PAYMENT_INFO_LABEL = By.cssSelector("#checkout_summary_container [data-test='payment-info-label']");
    private final By PAYMENT_INFO_VALUE = By.cssSelector("#checkout_summary_container [data-test='payment-info-value']");
    private final By SHIPPING_INFO_LABEL = By.cssSelector("#checkout_summary_container [data-test='shipping-info-label']");
    private final By SHIPPING_INFO_VALUE = By.cssSelector("#checkout_summary_container [data-test='shipping-info-value']");
    private final By TOTAL_INFO_LABEL = By.cssSelector("#checkout_summary_container [data-test='total-info-label']");
    private final By SUBTOTAL_LABEL = By.cssSelector("#checkout_summary_container [data-test='subtotal-label']");
    private final By TAX_LABEL = By.cssSelector("#checkout_summary_container [data-test='tax-label']");
    private final By TOTAL_LABEL = By.cssSelector("#checkout_summary_container [data-test='total-label']");
    private final By CANCEL_BUTTON = By.id("cancel");
    private final By FINISH_BUTTON = By.id("finish");

    public By getCheckoutSummaryContainer() {
        return CHECKOUT_SUMMARY_CONTAINER;
    }

    public By getCartList() {
        return CART_LIST;
    }

    public By getPaymentInfoLabel() {
        return PAYMENT_INFO_LABEL;
    }

    public By getPaymentInfoValue() {
        return PAYMENT_INFO_VALUE;
    }

    public By getShippingInfoLabel() {
        return SHIPPING_INFO_LABEL;
    }

    public By getShippingInfoValue() {
        return SHIPPING_INFO_VALUE;
    }

    public By getTotalInfoLabel() {
        return TOTAL_INFO_LABEL;
    }

    public By getSubtotalLabel() {
        return SUBTOTAL_LABEL;
    }

    public By getTaxLabel() {
        return TAX_LABEL;
    }

    public By getTotalLabel() {
        return TOTAL_LABEL;
    }

    public By getCancelButton() {
        return CANCEL_BUTTON;
    }

    public By getFinishButton() {
        return FINISH_BUTTON;
    }


}

package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.*;
import saucedemo.com.properties.ApplicationConfig;
import saucedemo.com.properties.SauceDemoConfig;
import saucedemo.com.stepdefinitions.Hooks;

public class ScenarioSetupActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(ScenarioSetupActions.class);
    @Steps
    private NavigationPage navigation;
    @Steps
    private InventoryPage inventory;
    @Steps
    private CartPage cart;
    @Steps
    private CheckoutInfoPage checkoutInfo;
    @Steps
    private CheckoutOverviewPage overview;
    @Steps
    private LoginActions loginActions;


    @Step("Prepare the page for the scenario: {0}")
    public void preparePage(String pageName) {
        String path = PageUrls.pathFor(pageName);
        getDriver().get(SauceDemoConfig.absoluteUrlFor("/"));
        if (!path.equals("/")) {
            loginActions.enter_login_credentials(
                    ApplicationConfig.required("standard_user.username"),
                    ApplicationConfig.required("all_users.password"));
            loginActions.click_login_button();
            switch (path) {
                case "/inventory.html" -> {
                    return;
                }
                case "/cart.html" -> {
                    $(navigation.getShoppingCartLink()).waitUntilClickable().click();
                    return;
                }
                case "/inventory-item.html" -> {
                    $(inventory.productTitleFor(4)).waitUntilClickable().click();
                    return;
                }
            }
            $(inventory.addToCartButtonFor("sauce-labs-backpack")).waitUntilClickable().click();
            $(navigation.getShoppingCartLink()).waitUntilClickable().click();
            $(cart.getCheckoutButton()).waitUntilClickable().click();
            $(checkoutInfo.getFirstNameInput()).waitUntilVisible();
            if (path.equals("/checkout-step-one.html")) return;
            $(checkoutInfo.getFirstNameInput()).type("Alex");
            $(checkoutInfo.getLastNameInput()).type("Taylor");
            $(checkoutInfo.getPostalCodeInput()).type("SW1A 1AA");
            $(checkoutInfo.getContinueButton()).waitUntilClickable().click();
            if (path.equals("/checkout-step-two.html")) return;
            $(overview.getFinishButton()).waitUntilClickable().click();
            log.info("Prepared the page for the scenario: {}", pageName);
        }
    }
}

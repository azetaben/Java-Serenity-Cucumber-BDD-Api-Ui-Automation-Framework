package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.*;
import saucedemo.com.properties.SauceDemoConfig;

public class ScenarioSetupActions extends UIInteractionSteps {
    @Steps
    private LoginPage login;
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


    @Step("Prepare the page for the scenario: {0}")
    public void preparePage(String pageName) {
        FrameworkLogger.actionStarted(ScenarioSetupActions.class, "preparePage");
        String path = PageUrls.pathFor(pageName);
        getDriver().get(SauceDemoConfig.absoluteUrlFor("/"));
        if (!path.equals("/")) {
            login.enterCredentials(
                    saucedemo.com.properties.ApplicationConfig.required("standard_user.username"),
                    saucedemo.com.properties.ApplicationConfig.required("all_users.password"));
            login.submitLogin();
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
        }
    }
}

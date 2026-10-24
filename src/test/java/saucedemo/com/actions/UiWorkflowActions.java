package saucedemo.com.actions;

import io.cucumber.datatable.DataTable;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.*;
import saucedemo.com.properties.ApplicationConfig;
import saucedemo.com.support.FrameworkLogger;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

public class UiWorkflowActions extends UIInteractionSteps {
    @Steps
    private LoginPage loginPage;
    @Steps
    private LoginActions loginActions;
    @Steps
    private CheckoutInfoPage checkoutInfoPage;
    @Steps
    private NavigationPage navigationPage;
    @Steps
    private InventoryPage inventoryPage;
    @Steps
    private CartPage cartPage;

    public String configured(String key) {
        return ApplicationConfig.required(key);
    }

    @Step("Assert login outcome: {0}")
    public void assert_login_outcome(String outcome) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Verify login outcome: " + outcome);
        String normalized = outcome.trim().toUpperCase(Locale.ROOT);

        switch (normalized) {
            case "SUCCESS" -> {
                waitForCondition().until(driver -> driver.getCurrentUrl().endsWith("/inventory.html"));
            }
            case "LOCKED_OUT" -> {
                String error = $(loginPage.getErrorMessage()).waitUntilVisible().getText();
                Assertions.assertTrue(error.contains("locked out"), "Unexpected locked-user error: " + error);
            }
            case "FAILURE", "INVALID", "PASSWORD_REQUIRED", "USERNAME_REQUIRED" -> {
                $(loginPage.getErrorMessage()).waitUntilVisible();
            }
            default -> throw new IllegalArgumentException("Unknown login outcome: " + outcome);
        }
    }

    @Step("Login as the default user")
    public void login_as_default_user() {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Login as the default user");
        loginActions.enter_login_credentials(ApplicationConfig.required("standard_user.username"),
                ApplicationConfig.required("all_users.password"));
        loginActions.click_login_button();
    }

    @Step("Assert login fields")
    public void assert_login_fields(DataTable table) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Verify login fields");
        for (String field : table.asList(String.class)) {
            switch (field.trim().toLowerCase(Locale.ROOT)) {
                case "username field" -> $(loginPage.getUsernameInput()).waitUntilVisible();
                case "password field" -> $(loginPage.getPasswordInput()).waitUntilVisible();
                case "login button" -> $(loginPage.getLoginButton()).waitUntilVisible();
                default -> throw new IllegalArgumentException("Unknown login form field: " + field);
            }
        }
    }

    @Step("Assert accepted users' credentials")
    public void assert_accepted_users_credentials(DataTable table) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Verify accepted users' credentials");
        String displayedCredentials = $(loginPage.getCredentialsContainer()).waitUntilVisible().getText();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            for (String value : row.values()) {
                if (value != null && !value.isBlank()) {
                    Assertions.assertTrue(displayedCredentials.contains(value.trim()),
                            "Login page does not advertise credential: " + value);
                }
            }
        }
    }

    @Step("Enter configured credentials for user: {0}")
    public void enter_configured_credentials(String username) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Enter configured credentials for user: " + username);
        loginActions.enter_login_credentials(username, ApplicationConfig.required("all_users.password"));
        loginActions.click_login_button();
    }

    @Step("Verify page: {0}")
    public void verify_page(String pageName) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Verify page: " + pageName);
        String expectedPath = PageUrls.pathFor(pageName);
        waitForCondition().until(driver -> URI.create(driver.getCurrentUrl()).getPath().equals(expectedPath));
    }

    @Step("Assert page title: {0}")
    public void assert_page_title(String expected) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Verify page title: " + expected);
        String actualHeading = $(navigationPage.getPageTitle()).waitUntilVisible().getText().trim();
        Assertions.assertEquals(expected, actualHeading, "Visible page heading");
    }

    @Step("Cancel checkout")
    public void cancel_checkout() {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Cancel checkout");
        $(checkoutInfoPage.getCancelButton()).waitUntilClickable().click();
    }

    @Step("Return to inventory")
    public void return_to_inventory() {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Return to inventory");
        $(navigationPage.getBackToProductsButton()).waitUntilClickable().click();
        waitForCondition().until(driver -> URI.create(driver.getCurrentUrl()).getPath()
                .equals(PageUrls.pathFor("inventory")));
        $(inventoryPage.getInventoryList()).waitUntilVisible();
    }

    @Step("Submit default customer details")
    public void submit_default_customer_details() {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Submit default customer details");
        $(checkoutInfoPage.getFirstNameInput()).type("Alex");
        $(checkoutInfoPage.getLastNameInput()).type("Taylor");
        $(checkoutInfoPage.getPostalCodeInput()).type("SW1A 1AA");
        $(checkoutInfoPage.getContinueButton()).waitUntilClickable().click();
    }

    @Step("Remove product from cart: {0}")
    public void remove_cart_product(String title) {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Remove product from cart: " + title);
        String slug = saucedemo.com.enums.Product.from(title).getSlug();
        $(cartPage.removeButtonFor(slug)).waitUntilClickable().click();
    }

    @Step("Add product to details")
    public void add_product_to_details() {
        FrameworkLogger.actionStarted(UiWorkflowActions.class, "Add product to details");
        By addButton = By.cssSelector("#inventory_item_container button[data-test='add-to-cart'],"
                + "#inventory_item_container button[id^='add-to-cart-']");
        $(addButton).waitUntilClickable().click();
    }
}

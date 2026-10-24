package saucedemo.com.actions;

import io.cucumber.datatable.DataTable;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.*;
import saucedemo.com.properties.ApplicationConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class UiWorkflowActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(UiWorkflowActions.class);
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
        String normalized = outcome.trim().toUpperCase(Locale.ROOT);

        switch (normalized) {
            case "SUCCESS" -> {
                waitForCondition().until(driver -> driver.getCurrentUrl().endsWith("/inventory.html"));
            }
            case "LOCKED_OUT" -> {
                String error = $(loginPage.getErrorMessage()).waitUntilVisible().getText();
                assertThat(error.contains("locked out")).as("Unexpected locked-user error: " + error).isTrue();
            }
            case "FAILURE", "INVALID", "PASSWORD_REQUIRED", "USERNAME_REQUIRED" -> {
                $(loginPage.getErrorMessage()).waitUntilVisible();
            }
            default -> throw new IllegalArgumentException("Unknown login outcome: " + outcome);
        }
        log.info("Asserted login outcome: {}", outcome);
    }

    @Step("Login as the default user")
    public void login_as_default_user() {
        loginActions.enter_login_credentials(ApplicationConfig.required("standard_user.username"),
                ApplicationConfig.required("all_users.password"));
        loginActions.click_login_button();
        log.info("Logged in as the default user");
    }

    @Step("Assert login fields")
    public void assert_login_fields(DataTable table) {
        for (String field : table.asList(String.class)) {
            switch (field.trim().toLowerCase(Locale.ROOT)) {
                case "username field" -> $(loginPage.getUsernameInput()).waitUntilVisible();
                case "password field" -> $(loginPage.getPasswordInput()).waitUntilVisible();
                case "login button" -> $(loginPage.getLoginButton()).waitUntilVisible();
                default -> throw new IllegalArgumentException("Unknown login form field: " + field);
            }
        }
        log.info("Asserted login fields: {}", table.asList(String.class));
    }

    @Step("Assert accepted users' credentials")
    public void assert_accepted_users_credentials(DataTable table) {
        String displayedCredentials = $(loginPage.getCredentialsContainer()).waitUntilVisible().getText();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            for (String value : row.values()) {
                if (value != null && !value.isBlank()) {
                    assertThat(displayedCredentials.contains(value.trim())).as("Login page does not advertise credential: " + value).isTrue();
                }
            }
        }
        log.info("Asserted accepted users' credentials: {}", table.asList(String.class));
    }

    @Step("Enter configured credentials for user: {0}")
    public void enter_configured_credentials(String username) {
        loginActions.enter_login_credentials(username, ApplicationConfig.required("all_users.password"));
        loginActions.click_login_button();
        log.info("Entered configured credentials for user: {}", username);
    }

    @Step("Verify page: {0}")
    public void verify_page(String pageName) {
        String expectedPath = PageUrls.pathFor(pageName);
        waitForCondition().until(driver -> URI.create(driver.getCurrentUrl()).getPath().equals(expectedPath));
        log.info("Verified page: {}", pageName);
    }

    @Step("Assert page title: {0}")
    public void assert_page_title(String expected) {
        String actualHeading = $(navigationPage.getPageTitle()).waitUntilVisible().getText().trim();
        assertThat(actualHeading).as("Visible page heading").isEqualTo(expected);
        log.info("Asserted page title: {}", expected);
    }

    @Step("Cancel checkout")
    public void cancel_checkout() {
        $(checkoutInfoPage.getCancelButton()).waitUntilClickable().click();
        waitForCondition().until(driver -> URI.create(driver.getCurrentUrl()).getPath()
                .equals(PageUrls.pathFor("cart")));
        $(cartPage.getCheckoutButton()).waitUntilVisible();
        log.info("Cancelled checkout and returned to cart page: {}", PageUrls.pathFor("cart"));
    }

    @Step("Return to inventory")
    public void return_to_inventory() {
        $(navigationPage.getBackToProductsButton()).waitUntilClickable().click();
        waitForCondition().until(driver -> URI.create(driver.getCurrentUrl()).getPath()
                .equals(PageUrls.pathFor("inventory")));
        $(inventoryPage.getInventoryList()).waitUntilVisible();
        log.info("Returned to inventory page: {}", PageUrls.pathFor("inventory"));
    }

    @Step("Submit default customer details")
    public void submit_default_customer_details() {
        $(checkoutInfoPage.getFirstNameInput()).type("Alex");
        $(checkoutInfoPage.getLastNameInput()).type("Taylor");
        $(checkoutInfoPage.getPostalCodeInput()).type("SW1A 1AA");
        $(checkoutInfoPage.getContinueButton()).waitUntilClickable().click();
        log.info("Submitted default customer details: first name Alex, last name Taylor, postal code SW1A 1AA");
    }

    @Step("Remove product from cart: {0}")
    public void remove_cart_product(String title) {
        String slug = saucedemo.com.enums.Product.from(title).getSlug();
        $(cartPage.removeButtonFor(slug)).waitUntilClickable().click();
        log.info("Removed product from cart: {}", title);
    }

    @Step("Add product to the cart")
    public void add_product_to_the_cart() {
        By addButton = By.cssSelector("#inventory_item_container button[data-test='add-to-cart'],"
                + "#inventory_item_container button[id^='add-to-cart-']");
        $(addButton).waitUntilClickable().click();
        log.info("Added product to the cart");
    }
}

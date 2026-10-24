package saucedemo.com.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import saucedemo.com.actions.InventoryActions;
import saucedemo.com.actions.LoginActions;
import saucedemo.com.actions.UiWorkflowActions;
import saucedemo.com.enums.Product;
import saucedemo.com.enums.User;
import saucedemo.com.enums.UserType;
import saucedemo.com.helper.LoginPasswordResolver;
import saucedemo.com.utilities.ApiSessionPreloader;

import java.util.List;

public class LoginStepDefinitions {
    private final ApiSessionPreloader apiSessionPreloader = new ApiSessionPreloader();
    @Steps
    private LoginActions loginActions;
    @Steps
    private UiWorkflowActions workflow;
    @Steps
    private InventoryActions inventoryActions;

    @ParameterType("[A-Za-z0-9_ -]+")
    public User user(String value) {
        return User.fromAlias(value);
    }

    @When("I login with {string} and {string}")
    public void i_login_with_and(String username, String password) throws InterruptedException {
        loginActions.enter_submit_login_credentials(username, password);

    }

    @When("I log in as {string} using the {string} password")
    public void i_log_in_using_password_mode(String username, String passwordMode) throws InterruptedException {
        String password = LoginPasswordResolver.resolve(passwordMode,
                () -> workflow.configured("all_users.password"));
        loginActions.enter_submit_login_credentials(username, password);
    }

    @When("I login as accepted user")
    public void i_login_as_accepted_user() throws InterruptedException {
        workflow.login_as_default_user();
    }

    @When("I login in login as {string}")
    @When("I preload a session via API as {string}")
    public void i_preload_a_session_via_api_as(String username) {
        User user = User.fromAlias(username);
        if (user.getExpectedLoginError().isPresent()) {
            throw new IllegalArgumentException("Cannot preload an authenticated session for a locked user");
        }
        apiSessionPreloader.preloadSessionAndOpenInventory(ThucydidesWebDriverSupport.getDriver(),
                user.getStandardUser());
    }

    @Given("I have {int} items in the shopping cart")
    public void i_have_items_in_the_shopping_cart(int itemCount) {
        List<Product> setupProducts = List.of(Product.BACKPACK, Product.BIKE_LIGHT, Product.BOLT_T_SHIRT, Product.FLEECE_JACKET, Product.ONESIE);
        if (itemCount < 0 || itemCount > setupProducts.size()) {
            throw new IllegalArgumentException("This cart setup supports between 0 and "
                    + setupProducts.size() + " items");
        }

        apiSessionPreloader.preloadSessionAndOpenInventory(ThucydidesWebDriverSupport.getDriver(),
                User.STANDARD_USER.getStandardUser());
        setupProducts.stream().limit(itemCount).forEach(inventoryActions::add_to_cart);
    }


    @Then("I should see the login form")
    public void i_should_see_the_login_form() {
        loginActions.should_display_login_form();
    }

    @Then("login form should contain the following fields:")
    public void login_form_should_contain_the_following_fields(DataTable table) {
        workflow.assert_login_fields(table);
    }


    @Then("accepted( users) credentials should be visible in the login form:")
    public void login_users_credentials_should_be_visible_in_the_login_form(DataTable table) {
        workflow.assert_accepted_users_credentials(table);
    }

    @When("I enter the credentials for the {string} user")
    public void i_enter_the_credentials_for_the_user(String role) throws InterruptedException {
        workflow.enter_configured_credentials(role);
    }

    @When("I login as an accepted \"{user}\"")
    public void login_as(User user) throws InterruptedException {
        loginActions.log_in_as_account_user(user);
    }

    @When("I login as an accepted {string} user")
    public void i_login_as_an_accepted_user(String alias) throws InterruptedException {
        loginActions.login_as_alias(alias);
    }

    @When("Click the login button")
    public void i_submit_the_login_form() throws InterruptedException {
        loginActions.click_login();

    }

    @Then("the login result should be {string}")
    @Then("I should see the login result {string}")
    public void i_should_see_the_login_result(String result) {
        workflow.assert_login_outcome(result);

    }

    @When("I am logged into Sauce Demo with user {string}")
    public void i_am_logged_into_sauce_demo_with_user(String username) throws InterruptedException {
        loginActions.open_the_sauce_demo_login_page();
        loginActions.enter_submit_login_credentials(username, workflow.configured("all_users.password"));
        workflow.verify_page(saucedemo.com.contentdesign.PageNames.INVENTORY);
    }

    @And("the shared password should be visible in the login form")
    public void the_shared_password_should_be_visible_in_the_login_form() {
        loginActions.assert_all_user_accepted_password_visible();

    }

    @When("I attempt to log in as \"{user}\"")
    public void i_attempt_to_log_in_as(User user) throws InterruptedException {
        loginActions.log_in_as_account_user(user);
    }

    @Then("the login result should match the \"{user}\" account")
    public void the_login_result_should_match_the_account(User user) {
        workflow.verify_page(user.getExpectedPage());
        user.getExpectedLoginError().ifPresent(loginActions::verify_error_msg);
    }

    @When("I login as accepted \"{user}\"")
    public void i_login_as_accepted(User user) throws InterruptedException {
        loginActions.log_in_as_account_user(user);
    }

    @When("I login as an accepted user")
    public void i_login_as_an_accepted_user() throws InterruptedException {
        loginActions.i_login_as_user(UserType.STANDARD_USER);

    }

}

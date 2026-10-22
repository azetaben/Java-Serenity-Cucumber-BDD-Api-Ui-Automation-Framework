package saucedemo.com.stepdefinitions;

import saucedemo.com.support.FrameworkLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.LoginActions;
import saucedemo.com.actions.UiWorkflowActions;
import saucedemo.com.enums.User;
import saucedemo.com.enums.UserType;
import saucedemo.com.helper.LoginPasswordResolver;


public class LoginStepDefinitions {
    @Steps
    private LoginActions loginActions;
    @Steps
    private UiWorkflowActions workflow;

    @ParameterType("[A-Za-z0-9_ -]+")
    public User user(String value) {
        return User.fromAlias(value);
    }

    @When("I login with {string} and {string}")
    public void i_login_with_and(String username, String password) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_login_with_and");
        loginActions.enter_credentials(username, password);

    }

    @When("I log in as {string} using the {string} password")
    public void i_log_in_using_password_mode(String username, String passwordMode) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_log_in_using_password_mode");
        String password = LoginPasswordResolver.resolve(passwordMode,
                () -> workflow.configured("all_users.password"));
        loginActions.enter_credentials(username, password);
    }

    @When("I login as accepted user")
    public void i_login_as_accepted_user() {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_login_as_accepted_user");
        workflow.login_as_default_user();
    }


    @Then("I should see the login form")
    public void i_should_see_the_login_form() {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_should_see_the_login_form");
        loginActions.should_display_login_form();
    }

    @Then("login form should contain the following fields:")
    public void login_form_should_contain_the_following_fields(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "login_form_should_contain_the_following_fields");
        workflow.assert_login_fields(table);
    }


    @Then("login( users) credentials should be visible in the login form:")
    public void login_users_credentials_should_be_visible_in_the_login_form(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "login_users_credentials_should_be_visible_in_the_login_form");
        workflow.assert_advertised_users_credentials(table);
    }

    @When("I enter the credentials for the {string} user")
    public void i_enter_the_credentials_for_the_user(String role) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_enter_the_credentials_for_the_user");
        workflow.enter_configured_credentials(role);
    }

    @When("I login as an accepted \"{user}\"")
    public void login_as(User user) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "login_as");
        loginActions.log_in_as_account_user(user);
    }

    @When("I login as an accepted {string} user")
    public void i_login_as_an_accepted_user(String alias) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_login_as_an_accepted_user");
        loginActions.login_as_alias(alias);
    }

    @When("I submit the login form")
    public void i_submit_the_login_form() {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_submit_the_login_form");
        loginActions.click_login();

    }

    @Then("the login result should be {string}")
    @Then("I should see the login result {string}")
    public void i_should_see_the_login_result(String result) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_should_see_the_login_result");
        workflow.assert_login_outcome(result);

    }

    @When("I am logged into Sauce Demo with user {string}")
    public void i_am_logged_into_sauce_demo_with_user(String username) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_am_logged_into_sauce_demo_with_user");
        loginActions.open_the_sauce_demo_login_page();
        loginActions.enter_credentials(username, workflow.configured("all_users.password"));
        workflow.verify_page(saucedemo.com.contentdesign.PageNames.INVENTORY);
    }

    @And("the shared password should be visible in the login form")
    public void the_shared_password_should_be_visible_in_the_login_form() {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "the_shared_password_should_be_visible_in_the_login_form");
        loginActions.assert_all_user_accepted_password_visible();

    }

    @When("I attempt to log in as \"{user}\"")
    public void i_attempt_to_log_in_as(User user) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_attempt_to_log_in_as");
        loginActions.log_in_as_account_user(user);
    }

    @Then("the login result should match the \"{user}\" account")
    public void the_login_result_should_match_the_account(User user) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "the_login_result_should_match_the_account");
        workflow.verify_page(user.getExpectedPage());
        user.getExpectedLoginError().ifPresent(loginActions::verify_error_msg);
    }

    @When("I login as accepted \"{user}\"")
    public void i_login_as_accepted(User user) {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_login_as_accepted");
        loginActions.log_in_as_account_user(user);
    }

    @When("I login as an accepted user")
    public void i_login_as_an_accepted_user() {
        FrameworkLogger.stepDefinitionStarted(LoginStepDefinitions.class, "i_login_as_an_accepted_user");
        loginActions.i_login_as_user(UserType.STANDARD_USER);

    }

}

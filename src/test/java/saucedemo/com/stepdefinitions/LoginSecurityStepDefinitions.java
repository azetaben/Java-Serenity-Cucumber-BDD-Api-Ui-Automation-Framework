package saucedemo.com.stepdefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.LoginActions;
import saucedemo.com.actions.LoginSecurityActions;
import saucedemo.com.actions.UiWorkflowActions;

public class LoginSecurityStepDefinitions {
    @Steps
    private LoginSecurityActions loginSecurityActions;
    @Steps
    private LoginActions login;
    @Steps
    private UiWorkflowActions workflow;

    @When("I open the {string} page without logging in")
    public void i_open_the_page_without_logging_in(String page) {
        loginSecurityActions.open_page_unauthenticated(page);
    }

    @When("I attempt to log in as {string} with the shared password")
    public void i_attempt_to_log_in_with_the_shared_password(String username) throws InterruptedException {
        login.enter_submit_login_credentials(username, workflow.configured("all_users.password"));
    }

    @When("I navigate back in the browser")
    public void i_navigate_back_in_the_browser() {
        loginSecurityActions.navigate_back();
    }

    @Then("the password field should be masked")
    public void the_password_field_should_be_masked() {
        loginSecurityActions.password_should_be_masked();
    }

    @Then("the login error should contain {string}")
    public void the_login_error_should_contain(String fragment) {
        loginSecurityActions.login_error_should_contain(fragment);
    }

    @Then("I remember the login error")
    public void i_remember_the_login_error() {
        loginSecurityActions.remember_login_error();
    }

    @Then("the login error should be identical to the remembered error")
    public void the_login_error_should_be_identical() {
        loginSecurityActions.login_error_should_match_remembered();
    }

    @Then("no injected script should have run or been rendered")
    public void no_injected_script_should_have_run() {
        loginSecurityActions.no_injected_script_executed();
    }

    @Then("the session cookie should be {word}")
    public void the_session_cookie_should_be(String state) {
        if ("present".equals(state)) loginSecurityActions.session_cookie_should_be_present();
        else if ("absent".equals(state)) loginSecurityActions.session_cookie_should_be_absent();
        else throw new IllegalArgumentException("Expected 'present' or 'absent' but was: " + state);
    }
}

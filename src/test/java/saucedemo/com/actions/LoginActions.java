package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.enums.User;
import saucedemo.com.enums.UserType;
import saucedemo.com.pages.LoginPage;

public class LoginActions extends UIInteractionSteps {
    @Steps
    private LoginPage loginPage;

    @Step("Submit login credentials")
    public void enter_credentials(String username, String password) {
        FrameworkLogger.actionStarted(LoginActions.class, "enter_credentials");
        if (!getDriver().getCurrentUrl().equals(saucedemo.com.properties.SauceDemoConfig.absoluteUrlFor("/")))
            loginPage.open();
        loginPage.enterCredentials(username, password);
        loginPage.submitLogin();
    }

    @Step("Click login button")
    public void click_login() {
        FrameworkLogger.actionStarted(LoginActions.class, "click_login");
        loginPage.submitLogin();
    }

    @Step("Open the Sauce Demo login page")
    public void open_the_sauce_demo_login_page() {
        FrameworkLogger.actionStarted(LoginActions.class, "open_the_sauce_demo_login_page");
        loginPage.open();
    }

    @Step("Verify the login form is displayed")
    public void should_display_login_form() {
        FrameworkLogger.actionStarted(LoginActions.class, "should_display_login_form");
        $(loginPage.getLoginForm()).waitUntilVisible();
    }

    @Step("Verify the error message is: {0}")
    public void verify_error_msg(String expected) {
        FrameworkLogger.actionStarted(LoginActions.class, "verify_error_msg");
        Assertions.assertEquals(expected, $(loginPage.getErrorMessage()).waitUntilVisible().getText().trim());
    }

    @Step("Dismiss the error message")
    public void dismiss_error() {
        FrameworkLogger.actionStarted(LoginActions.class, "dismiss_error");
        $(loginPage.getDismissErrorButton()).waitUntilClickable().click();
    }

    @Step("Verify the current URL is: {0}")
    public void verify_url(String url) {
        FrameworkLogger.actionStarted(LoginActions.class, "verify_url");
        waitForCondition().until(d -> d.getCurrentUrl().equals(url));
    }

    @Step("Log in as account user: {0}")
    public void log_in_as_account_user(User user) {
        FrameworkLogger.actionStarted(LoginActions.class, "log_in_as_account_user");
        enter_credentials(user.getUsername(), user.getPassword());
    }

    @Step("Log in as alias: {0}")
    public void login_as_alias(String alias) {
        FrameworkLogger.actionStarted(LoginActions.class, "login_as_alias");
        log_in_as_account_user(User.fromAlias(alias));
    }

    @Step("Log in as user type: {0}")
    public void i_login_as_user(UserType type) {
        FrameworkLogger.actionStarted(LoginActions.class, "i_login_as_user");
        log_in_as_account_user(User.valueOf(type.name()));
    }

    @Step("Verify the shared password is visible")
    public void assert_all_user_accepted_password_visible() {
        FrameworkLogger.actionStarted(LoginActions.class, "assert_all_user_accepted_password_visible");
        String displayedPassword = $(loginPage.getSharedPassword()).waitUntilVisible().getText()
                .replaceFirst("(?i)^Password for all users:\\s*", "")
                .trim();
        Assertions.assertEquals(saucedemo.com.properties.ApplicationConfig.required("all_users.password"), displayedPassword,
                "The login page should display the shared password");
    }
}

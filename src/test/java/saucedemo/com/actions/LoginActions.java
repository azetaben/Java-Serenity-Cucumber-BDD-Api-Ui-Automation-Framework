package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.enums.User;
import saucedemo.com.enums.UserType;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.properties.SauceDemoConfig;
import saucedemo.com.support.FrameworkLogger;
import saucedemo.com.support.GooglePasswordManager;

public class LoginActions extends UIInteractionSteps {
    @Steps
    private LoginPage loginPage;

    @Step("Submit login credentials")
    public void enter_credentials(String username, String password) {
        FrameworkLogger.actionStarted(LoginActions.class, "Submit login credentials");
        if (!getDriver().getCurrentUrl().equals(SauceDemoConfig.absoluteUrlFor("/")))
            loginPage.open();
        enter_login_credentials(username, password);
        click_login_button();
    }

    @Step("Click login button")
    public void click_login() {
        FrameworkLogger.actionStarted(LoginActions.class, "Click login button");
        click_login_button();
    }

    @Step("Enter login credentials")
    public void enter_login_credentials(String username, String password) {
        FrameworkLogger.pageOperationStarted(LoginPage.class, "Enter login credentials");
        $(loginPage.getUsernameInput()).waitUntilClickable().clear();
        FrameworkLogger.pageOperationStarted(LoginPage.class, "Enter username: " + username);
        $(loginPage.getUsernameInput()).type(username);
        $(loginPage.getPasswordInput()).waitUntilClickable().clear();
        FrameworkLogger.pageOperationStarted(LoginPage.class, "Enter password: " + password);
        $(loginPage.getPasswordInput()).type(password);
    }

    @Step("Click on the login button")
    public void click_login_button() {
        FrameworkLogger.pageOperationStarted(LoginPage.class, "Click login button");
        $(loginPage.getLoginButton()).waitUntilClickable().click();
        GooglePasswordManager.dismissIfVisible(getDriver());
    }

    @Step("Open the Sauce Demo login page")
    public void open_the_sauce_demo_login_page() {
        FrameworkLogger.actionStarted(LoginActions.class, "Open the Sauce Demo login page");
        loginPage.open();
    }

    @Step("Verify the login form is displayed")
    public void should_display_login_form() {
        FrameworkLogger.actionStarted(LoginActions.class, "Verify the login form is displayed");
        $(loginPage.getLoginForm()).waitUntilVisible();
    }

    @Step("Verify the error message is: {0}")
    public void verify_error_msg(String expected) {
        FrameworkLogger.actionStarted(LoginActions.class, "Verify the error message is: " + expected);
        Assertions.assertEquals(expected, $(loginPage.getErrorMessage()).waitUntilVisible().getText().trim());
    }

    @Step("Dismiss the error message")
    public void dismiss_error() {
        FrameworkLogger.actionStarted(LoginActions.class, "Dismiss the error message");
        $(loginPage.getDismissErrorButton()).waitUntilClickable().click();
    }

    @Step("Verify the current URL is: {0}")
    public void verify_url(String url) {
        FrameworkLogger.actionStarted(LoginActions.class, "Verify the current URL is: " + url);
        waitForCondition().until(d -> d.getCurrentUrl().equals(url));
    }

    @Step("Log in as account user: {0}")
    public void log_in_as_account_user(User user) throws InterruptedException {
        FrameworkLogger.actionStarted(LoginActions.class, "Log in as account user: " + user.getUsername());
        enter_credentials(user.getUsername(), user.getPassword());
    }

    @Step("Log in as alias: {0}")
    public void login_as_alias(String alias) throws InterruptedException {
        FrameworkLogger.actionStarted(LoginActions.class, "Log in as alias: " + alias);
        log_in_as_account_user(User.fromAlias(alias));
    }

    @Step("Log in as user type: {0}")
    public void i_login_as_user(UserType type) throws InterruptedException {
        FrameworkLogger.actionStarted(LoginActions.class, "Log in as user type: " + type);
        log_in_as_account_user(User.valueOf(type.name()));
    }

    @Step("Verify the shared password is visible")
    public void assert_all_user_accepted_password_visible() {
        FrameworkLogger.actionStarted(LoginActions.class, "Verify the shared password is visible");
        String displayedPassword = $(loginPage.getSharedPassword()).waitUntilVisible().getText()
                .replaceFirst("(?i)^Password for all users:\\s*", "")
                .trim();
        Assertions.assertEquals(saucedemo.com.properties.ApplicationConfig.required("all_users.password"), displayedPassword,
                "The login page should display the shared password");
    }
}

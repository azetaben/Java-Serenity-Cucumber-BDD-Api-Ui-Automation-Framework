package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.enums.User;
import saucedemo.com.enums.UserType;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.properties.SauceDemoConfig;
import saucedemo.com.support.GooglePasswordManager;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(LoginActions.class);
    @Steps
    private LoginPage loginPage;

    @Step("Enter and Submit login credentials")
    public void enter_submit_login_credentials(String username, String password) {
        if (!getDriver().getCurrentUrl().equals(SauceDemoConfig.absoluteUrlFor("/")))
            loginPage.open();
        enter_login_credentials(username, password);
        click_login_button();
    }

    @Step("Click login button")
    public void click_login() {
        click_login_button();
    }

    @Step("Enter login credentials")
    public void enter_login_credentials(String username, String password) {
        $(loginPage.getUsernameInput()).waitUntilClickable().clear();
        $(loginPage.getUsernameInput()).type(username);
        log.info("Entered username: {}", username);
        $(loginPage.getPasswordInput()).waitUntilClickable().clear();
        $(loginPage.getPasswordInput()).type(password);
        log.info("Entered password: {}", password);
    }

    @Step("Click on the login button")
    public void click_login_button() {
        log.info("Clicked on {} button", $(loginPage.getLoginButton()).waitUntilVisible().getText().trim());
        $(loginPage.getLoginButton()).waitUntilClickable().click();
        var config = EnvironmentSpecificConfiguration.from(
                SystemEnvironmentVariables.currentEnvironmentVariables());
        if (!config.getBooleanProperty("framework.passwordManager.observe", false)) {
            GooglePasswordManager.dismissIfVisible(getDriver());
        }
            log.info("Clicked on the login button");}

    @Step("Open the Sauce Demo login page")
    public void open_the_sauce_demo_login_page() {
        loginPage.open();
        log.info("Opened the Sauce Demo login page");
    }

    @Step("Verify the login form is displayed")
    public void should_display_login_form() {
        $(loginPage.getLoginForm()).waitUntilVisible();
        log.info("Verified the login form is displayed");
    }

    @Step("Verify the error message is: {0}")
    public void verify_error_msg(String expected) {
        assertThat($(loginPage.getErrorMessage()).waitUntilVisible().getText().trim()).isEqualTo(expected);
        log.info("Verify the error message is: {}", expected);
    }

    @Step("Dismiss the error message")
    public void close_the_google_password_popup_alert() {;
        $(loginPage.getDismissErrorButton()).waitUntilClickable().click();
        log.info("Dismissed the error message");
    }

    @Step("Verify the current URL is: {0}")
    public void verify_url(String url) {
        waitForCondition().until(d -> d.getCurrentUrl().equals(url));
        log.info("Verify the current URL is: {}", url);
        assertThat(getDriver().getCurrentUrl()).isEqualTo(url);
        log.info("Checked if the current URL is: {}", url);
    }

    @Step("Log in as account user: {0}")
    public void log_in_as_account_user(User user) throws InterruptedException {
        enter_submit_login_credentials(user.getStandardUser(), user.getPassword());
        log.info("Logged in as account user: {}", user.getStandardUser());
    }

    @Step("Log in as alias: {0}")
    public void login_as_alias(String alias) throws InterruptedException {
        log_in_as_account_user(User.fromAlias(alias));
        log.info("Logged in as alias: {}", alias);
    }

    @Step("Log in as user type: {0}")
    public void i_login_as_user(UserType type) throws InterruptedException {
        log_in_as_account_user(User.valueOf(type.name()));
        log.info("Logged in as user type: {}", type);
    }

    @Step("Verify the shared password is visible")
    public void assert_all_user_accepted_password_visible() {
        String displayedPassword = $(loginPage.getSharedPassword()).waitUntilVisible().getText()
                .replaceFirst("(?i)^Password for all users:\\s*", "")
                .trim();
        assertThat(displayedPassword).as("The login page should display the shared password").isEqualTo(saucedemo.com.properties.ApplicationConfig.required("all_users.password"));
        log.info("Verified the shared password is visible: {}", displayedPassword);
    }
}

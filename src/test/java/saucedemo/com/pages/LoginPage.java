package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import saucedemo.com.support.FrameworkLogger;

public class LoginPage extends PageObject {

    private final By LOGIN_CONTAINER = By.cssSelector("[data-test='login-container']");
    private final By LOGIN_LOGO = By.cssSelector(".login_logo");
    private final By LOGIN_FORM = By.cssSelector("form[aria-label='Login']");
    private final By USERNAME_INPUT = By.id("user-name");
    private final By PASSWORD_INPUT = By.id("password");
    private final By LOGIN_BUTTON = By.id("login-button");
    private final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private final By DISMISS_ERROR_BUTTON = By.cssSelector("[data-test='error-button']");
    private final By USERNAME_ERROR_ICON = By.cssSelector("#user-name + svg.error_icon");
    private final By PASSWORD_ERROR_ICON = By.cssSelector("#password + svg.error_icon");
    private final By CREDENTIALS_CONTAINER = By.cssSelector("[data-test='login-credentials-container']");
    private final By ACCEPTED_USERNAMES = By.cssSelector("[data-test='login-credentials']");
    private final By SHARED_PASSWORD = By.cssSelector("[data-test='login-password']");

    public By getLoginContainer() {
        return LOGIN_CONTAINER;
    }

    public By getLoginLogo() {
        return LOGIN_LOGO;
    }

    public By getLoginForm() {
        return LOGIN_FORM;
    }

    public By getUsernameInput() {
        return USERNAME_INPUT;
    }

    public By getPasswordInput() {
        return PASSWORD_INPUT;
    }

    public By getLoginButton() {
        return LOGIN_BUTTON;
    }

    public By getErrorMessage() {
        return ERROR_MESSAGE;
    }

    public By getDismissErrorButton() {
        return DISMISS_ERROR_BUTTON;
    }

    public By getUsernameErrorIcon() {
        return USERNAME_ERROR_ICON;
    }

    public By getPasswordErrorIcon() {
        return PASSWORD_ERROR_ICON;
    }

    public By getCredentialsContainer() {
        return CREDENTIALS_CONTAINER;
    }

    public By getAcceptedUsernames() {
        return ACCEPTED_USERNAMES;
    }

    public By getSharedPassword() {
        return SHARED_PASSWORD;
    }

    public void enterCredentials(String username, String password) {
        FrameworkLogger.pageOperationStarted(LoginPage.class, "enterCredentials");
        $(USERNAME_INPUT).waitUntilClickable().clear();
        $(USERNAME_INPUT).type(username);
        $(PASSWORD_INPUT).waitUntilClickable().clear();
        $(PASSWORD_INPUT).type(password);
    }

    public void submitLogin() {
        FrameworkLogger.pageOperationStarted(LoginPage.class, "submitLogin");
        $(LOGIN_BUTTON).waitUntilClickable().click();
    }

}

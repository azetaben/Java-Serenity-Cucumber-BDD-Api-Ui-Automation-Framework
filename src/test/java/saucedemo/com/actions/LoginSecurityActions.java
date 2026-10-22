package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.properties.SauceDemoConfig;

public class LoginSecurityActions extends UIInteractionSteps {
    @Steps
    private LoginPage loginPage;
    private String rememberedError;

    @Step("Open page unauthenticated: {0}")
    public void open_page_unauthenticated(String page) {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "open_page_unauthenticated");
        getDriver().get(SauceDemoConfig.absoluteUrlFor(saucedemo.com.contentdesign.PageUrls.pathFor(page)));
    }

    @Step("Navigate back")
    public void navigate_back() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "navigate_back");
        getDriver().navigate().back();
    }

    @Step("Navigate forward")
    public void navigate_forward() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "navigate_forward");
        getDriver().navigate().forward();
    }

    @Step("Verify password is masked")
    public void password_should_be_masked() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "password_should_be_masked");
        Assertions.assertEquals("password", $(loginPage.getPasswordInput()).getAttribute("type"));
    }

    @Step("Verify login error contains: {0}")
    public void login_error_should_contain(String fragment) {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "login_error_should_contain");
        Assertions.assertTrue($(loginPage.getErrorMessage()).waitUntilVisible().getText().contains(fragment));
    }

    @Step("Remember login error")
    public void remember_login_error() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "remember_login_error");
        rememberedError = $(loginPage.getErrorMessage()).waitUntilVisible().getText();
    }

    @Step("Verify login error matches remembered")
    public void login_error_should_match_remembered() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "login_error_should_match_remembered");
        Assertions.assertEquals(rememberedError, $(loginPage.getErrorMessage()).waitUntilVisible().getText());
    }

    @Step("Verify no injected script executed")
    public void no_injected_script_executed() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "no_injected_script_executed");
        Assertions.assertEquals(0, getDriver().findElements(By.cssSelector("script[data-injected='true']")).size());
    }

    @Step("Verify session cookie is present")
    public void session_cookie_should_be_present() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "session_cookie_should_be_present");
        Assertions.assertNotNull(getDriver().manage().getCookieNamed("session-username"));
    }

    @Step("Verify session cookie is absent")
    public void session_cookie_should_be_absent() {
        FrameworkLogger.actionStarted(LoginSecurityActions.class, "session_cookie_should_be_absent");
        Assertions.assertNull(getDriver().manage().getCookieNamed("session-username"));
    }
}

package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.properties.SauceDemoConfig;
import saucedemo.com.stepdefinitions.Hooks;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSecurityActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(LoginSecurityActions.class);
    @Steps
    private LoginPage loginPage;
    private String rememberedError;

    @Step("Open page unauthenticated: {0}")
    public void open_page_unauthenticated(String page) {
        getDriver().get(SauceDemoConfig.absoluteUrlFor(saucedemo.com.contentdesign.PageUrls.pathFor(page)));
        log.info("Opened page unauthenticated: {}", page);
    }

    @Step("Navigate back")
    public void navigate_back() {
        getDriver().navigate().back();
        log.info("Navigated back");
    }

    @Step("Navigate forward")
    public void navigate_forward() {
        getDriver().navigate().forward();
        log.info("Navigated forward");
    }

    @Step("Verify password is masked")
    public void password_should_be_masked() {
        assertThat($(loginPage.getPasswordInput()).getAttribute("type")).isEqualTo("password");
        log.info("Verified password is masked");
    }

    @Step("Verify login error contains: {0}")
    public void login_error_should_contain(String fragment) {
        log.info("Verify login error contains: {}", fragment);
        assertThat($(loginPage.getErrorMessage()).waitUntilVisible().getText().contains(fragment)).isTrue();
    }

    @Step("Remember login error")
    public void remember_login_error() {
        log.info("Remember login error");
        rememberedError = $(loginPage.getErrorMessage()).waitUntilVisible().getText();
    }

    @Step("Verify login error matches remembered")
    public void login_error_should_match_remembered() {
        log.info("Verify login error matches remembered");
        assertThat($(loginPage.getErrorMessage()).waitUntilVisible().getText()).isEqualTo(rememberedError);
    }

    @Step("Verify no injected script executed")
    public void no_injected_script_executed() {
        log.info("Verify no injected script executed");
        assertThat(getDriver().findElements(By.cssSelector("script[data-injected='true']")).size()).isEqualTo(0);
    }

    @Step("Verify session cookie is present")
    public void session_cookie_should_be_present() {
        log.info("Verify session cookie is present");
        assertThat((Object) getDriver().manage().getCookieNamed("session-username")).isNotNull();
    }

    @Step("Verify session cookie is absent")
    public void session_cookie_should_be_absent() {
        log.info("Verify session cookie is absent");
        assertThat((Object) getDriver().manage().getCookieNamed("session-username")).isNull();
    }
}

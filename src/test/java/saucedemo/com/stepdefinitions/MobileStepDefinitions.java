package saucedemo.com.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import saucedemo.com.Appium.MobileSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class MobileStepDefinitions {
    @Before(value = "@Mobile", order = 1)
    public void validateMobileEnvironment(Scenario scenario) {
        MobileSupport.validate(SystemEnvironmentVariables.currentEnvironmentVariables(),
                scenario.getSourceTagNames().contains("@NativeApp"));
    }

    @Given("I start the configured Appium application")
    @Given("I start the configured mobile application")
    public void startConfiguredApplication() {
        MobileSupport.appiumDriver(ThucydidesWebDriverSupport.getDriver());
    }

    @Then("the Appium session is active")
    public void appiumSessionIsActive() {
        assertThat(MobileSupport.appiumDriver(ThucydidesWebDriverSupport.getDriver()).getSessionId()).isNotNull();
    }
}

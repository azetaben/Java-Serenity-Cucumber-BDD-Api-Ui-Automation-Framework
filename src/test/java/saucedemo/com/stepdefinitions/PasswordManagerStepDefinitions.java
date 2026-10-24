package saucedemo.com.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import saucedemo.com.support.GooglePasswordManager;

public class PasswordManagerStepDefinitions {
    @Given("native Password Manager observation is enabled")
    public void observationIsEnabled() {
        var config = EnvironmentSpecificConfiguration.from(SystemEnvironmentVariables.currentEnvironmentVariables());
        if (!config.getBooleanProperty("framework.passwordManager.observe", false)) {
            throw new IllegalStateException("Run this desktop-only scenario with mvn verify -Ppassword-manager");
        }
        if (config.getBooleanProperty("headless.mode", false)) {
            throw new IllegalStateException("Password Manager observation requires -Dheadless.mode=false");
        }
    }

    @Then("I wait up to {int} seconds for Google Password Manager to appear")
    public void waitForPasswordManager(int seconds) {
        GooglePasswordManager.waitUntilVisible(ThucydidesWebDriverSupport.getDriver(), seconds);
    }
}

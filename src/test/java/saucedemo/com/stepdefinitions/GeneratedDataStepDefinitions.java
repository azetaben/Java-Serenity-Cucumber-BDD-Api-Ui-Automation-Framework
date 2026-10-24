package saucedemo.com.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.actions.CheckoutInfoActions;
import saucedemo.com.actions.LoginActions;
import saucedemo.com.support.FrameworkLogger;
import saucedemo.com.utilities.TestDataGeneration;

/**
 * Scenario-owned generator; UI operations remain in Serenity action classes.
 */
public class GeneratedDataStepDefinitions {
    private static final Logger log = LoggerFactory.getLogger(GeneratedDataStepDefinitions.class);
    @Steps
    private LoginActions loginActions;
    @Steps
    private CheckoutInfoActions checkoutInfoActions;
    private TestDataGeneration data;

    @Given("I generate test data using seed {long}")
    public void use_seed(long seed) {
        FrameworkLogger.stepDefinitionStarted(GeneratedDataStepDefinitions.class, "use_seed");
        log.info("Generate scenario test data using seed: {}", seed);
        data = new TestDataGeneration(seed);
    }

    @When("I log in using generated {string} credentials")
    public void log_in_using_generated_credentials(String caseName) throws InterruptedException {
        FrameworkLogger.stepDefinitionStarted(GeneratedDataStepDefinitions.class, "log_in_using_generated_credentials");
        log.info("Log in using generated credentials case: {}", caseName);
        var account = generator().login(caseName);
        loginActions.enter_credentials(account.username(), account.password());
    }

    @When("I enter generated {string} checkout information")
    public void enter_generated_checkout_information(String caseName) {
        FrameworkLogger.stepDefinitionStarted(GeneratedDataStepDefinitions.class, "enter_generated_checkout_information");
        log.info("Enter generated checkout information case: {}", caseName);
        checkoutInfoActions.enter_checkout_information(generator().checkout(caseName));
    }

    private TestDataGeneration generator() {
        if (data == null) data = TestDataGeneration.configured();
        return data;
    }
}

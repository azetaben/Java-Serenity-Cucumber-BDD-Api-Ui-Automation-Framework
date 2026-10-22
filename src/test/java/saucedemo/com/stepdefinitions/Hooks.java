package saucedemo.com.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Failure diagnostics for UI scenarios; browser lifecycle stays with Serenity.
 */
public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before(order = 0)
    public void recordEnvironmentAndEnforceProductionSafety(Scenario scenario) {
        String environment = System.getProperty("test.environment",
                System.getenv().getOrDefault("TEST_ENVIRONMENT", "local")).trim().toLowerCase(Locale.ROOT);
        log.info("Starting scenario '{}' in environment '{}' with provider '{}'", scenario.getName(), environment,
                System.getProperty("environment", System.getenv().getOrDefault("EXECUTION_PROVIDER", "local")));
        scenario.attach(environment.getBytes(StandardCharsets.UTF_8), "text/plain", "Target environment");
        scenario.attach(System.getProperty("environment", System.getenv().getOrDefault("EXECUTION_PROVIDER", "local"))
                .getBytes(StandardCharsets.UTF_8), "text/plain", "Execution provider");
        scenario.attach(System.getProperty("cucumber.filter.tags", "@CartPrice and not @manual")
                .getBytes(StandardCharsets.UTF_8), "text/plain", "Cucumber tag filter");

        if (environment.equals("production") && !ProductionSafeScenarios.isAllowed(
                scenario.getUri(), scenario.getName(), scenario.getSourceTagNames())) {
            log.error("Blocked scenario '{}' because it is not in the production-safe allowlist", scenario.getName());
            throw new IllegalStateException("Production scenario is not on the reviewed @ProductionSafe allowlist: "
                    + scenario.getUri() + " :: " + scenario.getName());
        }
    }

    @After(order = 100)
    public void logScenarioOutcome(Scenario scenario) {
        log.info("Scenario '{}' finished with status {}", scenario.getName(), scenario.getStatus());
    }

    @After(value = "@UI", order = 10)
    public void captureFailureContext(Scenario scenario) {
        if (!scenario.isFailed() || !ThucydidesWebDriverSupport.isDriverInstantiated()) return;
        try {
            WebDriver driver = ThucydidesWebDriverSupport.getDriver();
            String context = "url=" + driver.getCurrentUrl() + ", title=" + driver.getTitle();
            log.warn("UI scenario failed [{}]: {}", scenario.getName(), context);
            scenario.attach(context.getBytes(StandardCharsets.UTF_8), "text/plain", "Failure context");
            if (driver instanceof TakesScreenshot screenshotDriver) {
                scenario.attach(screenshotDriver.getScreenshotAs(OutputType.BYTES), "image/png", "Failure screenshot");
            }
        } catch (RuntimeException e) {
            log.warn("Could not capture failure context: {}", e.toString());
        }
    }
}

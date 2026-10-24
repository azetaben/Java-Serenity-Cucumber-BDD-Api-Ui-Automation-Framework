package saucedemo.com.properties;

import com.typesafe.config.ConfigFactory;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class SerenityConfigurationTest {
    @ParameterizedTest
    @ValueSource(strings = {"default", "chrome", "firefox", "edge", "safari",
            "grid", "remote", "saucelabs", "browserstack"})
    void selectedProfileBuildsCapabilitiesWithTimeoutsAndOnlyItsOwnBrowserOptions(String profile) {
        var variables = SystemEnvironmentVariables.createEnvironmentVariables();
        // Isolate profile checks from the headless flag supplied to a Maven UI run.
        variables.setProperty("headless.mode", "false");
        variables.setProperty("environment", profile);
        var configuration = EnvironmentSpecificConfiguration.from(variables);
        // Exercise Serenity's converter, not just HOCON parsing: this catches binary
        // incompatibilities between Serenity and Selenium before a browser is started.
        var capabilities = W3CCapabilities.definedIn(variables)
                .withPrefix("webdriver.capabilities").asDesiredCapabilities();
        String browser = switch (profile) {
            case "firefox", "safari" -> profile;
            case "edge" -> "MicrosoftEdge";
            default -> "chrome";
        };
        assertThat(capabilities.getBrowserName()).isEqualTo(browser);
        assertThat(capabilities.getCapability("timeouts")).isEqualTo(
                Map.of("implicit", 0, "pageLoad", 60000, "script", 30000));
        assertThat(configuration.getProperty("webdriver.wait.for.timeout")).isEqualTo("10000");
        assertThat(configuration.getProperty("webdriver.timeouts.implicitlywait")).isEqualTo("0");
        assertThat(capabilities.getCapabilityNames().contains("goog:chromeOptions"))
                .isEqualTo(browser.equals("chrome"));
        assertThat(capabilities.getCapabilityNames().contains("moz:firefoxOptions"))
                .isEqualTo(browser.equals("firefox"));
        assertThat(capabilities.getCapabilityNames().contains("ms:edgeOptions"))
                .isEqualTo(browser.equals("MicrosoftEdge"));
        if (browser.equals("chrome")) {
            var options = W3CCapabilities.definedIn(variables)
                    .withPrefix("webdriver.capabilities").chromeOptions();
            var chromeOptions = (Map<?, ?>) options.getCapability("goog:chromeOptions");
            assertThat((java.util.List<?>) chromeOptions.get("args"))
                    .anyMatch("--incognito"::equals);
            var prefs = (Map<?, ?>) chromeOptions.get("prefs");
            for (String preference : new String[]{"credentials_enable_service", "credentials_enable_autosignin",
                    "profile.password_manager_enabled", "profile.password_manager_leak_detection"}) {
                assertThat(prefs.get(preference)).as("%s: %s", profile, preference).isEqualTo(false);
            }
        }
        if (profile.equals("safari")) {
            assertThat(configuration.getProperty("webdriver.autodownload")).isEqualTo("false");
            assertThat(configuration.getProperty("headless.mode")).isEqualTo("false");
        }
    }

    @Test
    void passwordManagerObservationProfileAllowsNativePrompts() {
        var variables = SystemEnvironmentVariables.createEnvironmentVariables();
        // Isolate profile checks from the headless flag supplied to a Maven UI run.
        variables.setProperty("headless.mode", "false");
        variables.setProperty("environment", "chrome-password-manager");
        var config = EnvironmentSpecificConfiguration.from(variables);
        assertThat(config.getBooleanProperty("framework.passwordManager.observe", false)).isTrue();
        assertThat(config.getBooleanProperty("headless.mode", false)).isFalse();
        var options = W3CCapabilities.definedIn(variables)
                .withPrefix("webdriver.capabilities").chromeOptions();
        var chrome = (Map<?, ?>) options.getCapability("goog:chromeOptions");
        assertThat((java.util.List<?>) chrome.get("args"))
                .noneMatch("--incognito"::equals)
                .noneMatch(value -> value.toString().contains("--disable-features=PasswordLeakDetection"));
        var prefs = (Map<?, ?>) chrome.get("prefs");
        assertThat(prefs.get("credentials_enable_service")).isEqualTo(true);
        assertThat(prefs.get("profile.password_manager_enabled")).isEqualTo(true);
        assertThat(prefs.get("profile.password_manager_leak_detection")).isEqualTo(true);
    }

    @Test
    void explicitRunPropertiesOverrideTheSelectedEnvironment() {
        var variables = SystemEnvironmentVariables.createEnvironmentVariables();
        // Isolate profile checks from the headless flag supplied to a Maven UI run.
        variables.setProperty("headless.mode", "false");
        variables.setProperty("environment", "firefox");
        variables.setProperty("headless.mode", "true");
        variables.setProperty("webdriver.base.url", "https://example.test");
        var configuration = EnvironmentSpecificConfiguration.from(variables);
        assertThat(configuration.getProperty("headless.mode")).isEqualTo("true");
        assertThat(configuration.getProperty("webdriver.base.url")).isEqualTo("https://example.test");
    }

    @Test
    void propertiesFileDoesNotRedefineHoconSettings() throws IOException {
        var properties = new Properties();
        try (var input = Files.newInputStream(Path.of("serenity.properties"))) {
            properties.load(input);
        }
        var config = ConfigFactory.parseFile(new File("src/test/resources/serenity.conf")).resolve();
        for (String key : properties.stringPropertyNames()) {
            assertThat(config.hasPath(key)).as("Duplicate configuration key: %s", key).isFalse();
        }
    }
}

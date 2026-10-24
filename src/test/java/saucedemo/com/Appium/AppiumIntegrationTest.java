package saucedemo.com.Appium;

import com.sun.net.httpserver.HttpServer;
import io.appium.java_client.AppiumDriver;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

class AppiumIntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {"android-web", "ios-web", "android-native", "ios-native"})
    void serenityCreatesW3cSessionsWithoutDesktopCapabilities(String profile) throws Exception {
        var variables = isolatedVariables();
        variables.setProperty("environment", profile);
        variables.setProperty("appium.udid", "test-device");
        boolean nativeApp = profile.endsWith("native");
        if (nativeApp) variables.setProperty("appium.app", "/server/path/test-app");
        MobileSupport.validate(variables, nativeApp);
        boolean android = profile.startsWith("android");
        var provider = new net.thucydides.core.webdriver.ProvidedDriverConfiguration(variables);
        assertThat(provider.getDriverSource()).isInstanceOf(AppiumDriverSource.class);
        var capabilities = AppiumDriverSource.optionsFor(variables);
        assertThat(capabilities.getCapability("platformName").toString()).isEqualToIgnoringCase(android ? "Android" : "iOS");
        assertThat(capabilities.getCapability("appium:automationName")).isEqualTo(android ? "UiAutomator2" : "XCUITest");
        assertThat(capabilities.getCapability("appium:udid")).isEqualTo("test-device");
        assertThat(capabilities.getCapability("goog:chromeOptions")).isNull();
        assertThat(capabilities.getCapability("ms:edgeOptions")).isNull();
        assertThat(AppiumConfiguration.from(variables).getUrl().getPath()).isEqualTo("/");
        if (nativeApp) assertThat(capabilities.getBrowserName()).isEmpty();
        else assertThat(capabilities.getBrowserName()).isEqualTo(android ? "Chrome" : "Safari");

        // A local protocol stub exercises the real Appium/Selenium clients without a device.
        var request = new AtomicReference<String>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/session", exchange -> {
            if (exchange.getRequestMethod().equals("POST")) {
                request.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            }
            String body = exchange.getRequestMethod().equals("POST")
                    ? "{\"value\":{\"sessionId\":\"stub-session\",\"capabilities\":{\"platformName\":\""
                        + (android ? "Android" : "iOS") + "\"}}}"
                    : "{\"value\":null}";
            byte[] response = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (var output = exchange.getResponseBody()) { output.write(response); }
        });
        server.start();
        AppiumDriver driver = null;
        try {
            var url = new java.net.URI("http://127.0.0.1:" + server.getAddress().getPort() + "/").toURL();
            variables.setProperty("appium.hub", url.toString());
            driver = new AppiumDriverSource().newDriver(variables);
            assertThat(MobileSupport.appiumDriver(driver).getSessionId().toString()).isEqualTo("stub-session");
            assertThat(request.get()).contains("firstMatch", "appium:automationName", "appium:udid");
        } finally {
            if (driver != null) driver.quit();
            server.stop(0);
        }
    }

    @Test
    void missingDeviceAndWrongSuiteFailBeforeConnecting() {
        var variables = isolatedVariables();
        variables.setProperty("environment", "android-web");
        variables.setProperty("appium.udid", "");
        variables.clearProperty("environments.android-web.appium.udid");
        assertThatThrownBy(() -> MobileSupport.validate(variables, false))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("APPIUM_UDID");
        variables.setProperty("appium.udid", "test-device");
        assertThatThrownBy(() -> MobileSupport.validate(variables, true))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("matching");
        variables.setProperty("environment", "android-native");
        variables.setProperty("appium.app", "");
        variables.setProperty("appium.appPackage", "");
        assertThatThrownBy(() -> MobileSupport.validate(variables, true))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("APPIUM_APP");
    }
    private static SystemEnvironmentVariables isolatedVariables() {
        var variables = new SystemEnvironmentVariables(new java.util.Properties(), java.util.Map.of());
        variables.setConfig(com.typesafe.config.ConfigFactory.parseFile(
                new java.io.File("src/test/resources/serenity.conf")).resolve());
        return variables;
    }
}

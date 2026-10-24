package saucedemo.com.Appium;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.model.util.EnvironmentVariables;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;

/** Mobile preflight and access to the Serenity-managed Appium session. */
public final class MobileSupport {
    private MobileSupport() { }

    public static void validate(EnvironmentVariables variables, boolean nativeApp) {
        configureOverrides(variables);
        var config = EnvironmentSpecificConfiguration.from(variables);
        if (!"provided".equals(config.getProperty("webdriver.driver"))
                || !"appium".equals(config.getProperty("webdriver.provided.type"))) {
            throw new IllegalArgumentException("Mobile suites require an android-web, ios-web, android-native or ios-native environment");
        }
        if (config.getBooleanProperty("framework.mobile.native", false) != nativeApp) {
            throw new IllegalArgumentException("Select an Appium environment matching the web/native suite");
        }
        require(config, "appium.udid", "Set APPIUM_UDID or -Dappium.udid to select the device");
        if (nativeApp && config.getOptionalProperty("appium.app").filter(value -> !value.isBlank()).isEmpty()) {
            String platform = config.getProperty("appium.platformName");
            if ("Android".equalsIgnoreCase(platform)) {
                require(config, "appium.appPackage", "Provide APPIUM_APP or an installed Android package and activity");
                require(config, "appium.appActivity", "Provide APPIUM_APP_ACTIVITY for an installed Android app");
            } else {
                require(config, "appium.bundleId", "Provide APPIUM_APP or APPIUM_BUNDLE_ID for an installed iOS app");
            }
        }
    }

    private static void configureOverrides(EnvironmentVariables variables) {
        String environment = variables.getProperty("environment", "default");
        java.util.Map<String, String> overrides = java.util.Map.of(
                "appium.hub", "APPIUM_SERVER_URL", "appium.udid", "APPIUM_UDID",
                "appium.deviceName", "APPIUM_DEVICE_NAME", "appium.platformVersion", "APPIUM_PLATFORM_VERSION",
                "appium.app", "APPIUM_APP", "appium.appPackage", "APPIUM_APP_PACKAGE",
                "appium.appActivity", "APPIUM_APP_ACTIVITY", "appium.bundleId", "APPIUM_BUNDLE_ID");
        overrides.forEach((property, variable) -> {
            String value = variables.getProperty(property);
            if (value == null) value = System.getenv(variable);
            if (value != null) {
                // Explicit profile keys take precedence in Serenity 4.2.9.
                variables.setProperty("environments." + environment + "." + property, value);
            }
        });
    }
    private static void require(EnvironmentSpecificConfiguration config, String key, String message) {
        if (config.getOptionalProperty(key).filter(value -> !value.isBlank()).isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static AppiumDriver appiumDriver(WebDriver driver) {
        WebDriver actual = driver instanceof WebDriverFacade facade ? facade.getProxiedDriver() : driver;
        if (!(actual instanceof AppiumDriver mobile)) {
            throw new IllegalStateException("The current Serenity driver is not an Appium session");
        }
        return mobile;
    }
}

package saucedemo.com.Appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.webdriver.DriverSource;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

/** Serenity-managed driver source using Appium's W3C option normalization. */
public class AppiumDriverSource implements DriverSource {
    @Override
    public WebDriver newDriver() {
        return newDriver(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    AppiumDriver newDriver(EnvironmentVariables variables) {
        var config = EnvironmentSpecificConfiguration.from(variables);
        MobileSupport.validate(variables, config.getBooleanProperty("framework.mobile.native", false));
        var appium = AppiumConfiguration.from(variables);
        var options = optionsFor(variables);
        return options instanceof UiAutomator2Options
                ? new AndroidDriver(appium.getUrl(), options)
                : new IOSDriver(appium.getUrl(), options);
    }

    static BaseOptions<?> optionsFor(EnvironmentVariables variables) {
        var appium = AppiumConfiguration.from(variables);
        String platform = EnvironmentSpecificConfiguration.from(variables).getProperty("appium.platformName");
        BaseOptions<?> options;
        if (platform.equalsIgnoreCase("Android")) options = new UiAutomator2Options();
        else if (platform.equalsIgnoreCase("iOS")) options = new XCUITestOptions();
        else throw new IllegalArgumentException("Unsupported Appium platform: " + platform);
        appium.getCapabilities().asMap().forEach((key, value) -> {
            // hub selects the endpoint; platformName is supplied by the typed options.
            if (!key.equals("hub") && !key.equals("platformName")) options.setCapability(key, value);
        });
        return options;
    }

    @Override
    public boolean takesScreenshots() { return true; }

    @Override
    public Class<? extends WebDriver> driverType() { return AppiumDriver.class; }
}

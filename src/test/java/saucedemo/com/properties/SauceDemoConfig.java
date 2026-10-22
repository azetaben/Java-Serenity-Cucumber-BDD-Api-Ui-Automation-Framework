package saucedemo.com.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SauceDemoConfig {
    private static final Logger log = LoggerFactory.getLogger(SauceDemoConfig.class);

    private SauceDemoConfig() {
    }

    public static String baseUrl() {
        log.debug("Resolving SauceDemo base URL from the effective test environment");
        return ApplicationConfig.value("webdriver.base.url", "https://www.saucedemo.com");
    }

    public static String absoluteUrlFor(String path) {
        return baseUrl().replaceAll("/+$", "") + (path.startsWith("/") ? path : "/" + path);
    }
}

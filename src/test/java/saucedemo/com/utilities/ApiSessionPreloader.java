package saucedemo.com.utilities;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import saucedemo.com.api.SauceDemoLoginApi;
import saucedemo.com.properties.SauceDemoConfig;

import java.net.URI;

/**
 * Probes the app over HTTP, then seeds its client-side session cookie for UI setup.
 */
public class ApiSessionPreloader {
    private static final String SESSION_COOKIE = "session-username";

    private final SauceDemoLoginApi loginApi;

    public ApiSessionPreloader() {
        this(new SauceDemoLoginApi());

    }

    ApiSessionPreloader(SauceDemoLoginApi loginApi) {
        this.loginApi = loginApi;
    }

    public void preloadSessionAndOpenInventory(WebDriver driver, String username) {
        loginApi.checkApplicationAvailable();
        setSessionCookie(driver, username);
        driver.get(SauceDemoConfig.absoluteUrlFor("/inventory.html"));
    }

    public void setSessionCookie(WebDriver driver, String username) {
        String host = URI.create(SauceDemoConfig.baseUrl()).getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("Cannot determine the SauceDemo host for session cookie");
        }

        // WebDriver requires the browser to be on the cookie's domain before adding it.
        driver.get(SauceDemoConfig.absoluteUrlFor("/"));
        driver.manage().deleteCookieNamed(SESSION_COOKIE);
        driver.manage().addCookie(new Cookie(SESSION_COOKIE, username, host, "/", null,
                false, false, "Lax"));
    }
}

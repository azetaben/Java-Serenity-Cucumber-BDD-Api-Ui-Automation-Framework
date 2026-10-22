package saucedemo.com.support;

import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.properties.SauceDemoConfig;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UiChecks {
    private UiChecks() {
    }

    public static String resolvePagePath(String pageName) {
        return PageUrls.pathFor(pageName);
    }

    public static String currentUrl(UIInteractionSteps steps) {
        return steps.getDriver().getCurrentUrl();
    }

    public static String currentPagePath(UIInteractionSteps steps) {
        return URI.create(currentUrl(steps)).getPath();
    }

    public static boolean isOnPage(UIInteractionSteps steps, String pageName) {
        return resolvePagePath(pageName).equals(currentPagePath(steps));
    }

    public static void verifyCurrentPage(UIInteractionSteps steps, String pageName) {
        FrameworkLogger.assertionStarted(UiChecks.class, "verifyCurrentPage");
        String path = resolvePagePath(pageName);
        steps.waitForCondition().withMessage("Expected page '" + pageName + "' with path " + path)
                .until(ignored -> Objects.equals(path, currentPagePath(steps)));
    }

    public static void verifyAbsoluteUrl(UIInteractionSteps steps, String expectedUrl) {
        FrameworkLogger.assertionStarted(UiChecks.class, "verifyAbsoluteUrl");
        String destination = expectedUrl.startsWith("/") ? SauceDemoConfig.absoluteUrlFor(expectedUrl) : expectedUrl;
        steps.waitForCondition().withMessage("Expected destination URL " + destination)
                .until(ignored -> Objects.equals(destination, currentUrl(steps)));
    }

    public static void verifyCurrentPagePath(UIInteractionSteps steps, String expectedPath) {
        FrameworkLogger.assertionStarted(UiChecks.class, "verifyCurrentPagePath");
        URI path = URI.create(expectedPath);
        if (!expectedPath.startsWith("/") || path.isAbsolute() || path.getRawAuthority() != null
                || path.getRawQuery() != null || path.getRawFragment() != null) {
            throw new IllegalArgumentException("Expected an absolute URL path without query or fragment: " + expectedPath);
        }
        steps.waitForCondition().withMessage("Expected page URL path " + expectedPath)
                .until(ignored -> Objects.equals(expectedPath, currentPagePath(steps)));
    }


    public static String readText(UIInteractionSteps steps, By locator) {
        return steps.$(locator).waitUntilVisible().getText().trim();
    }

    public static void assertTextEquals(UIInteractionSteps steps, By locator, String expected) {
        FrameworkLogger.assertionStarted(UiChecks.class, "assertTextEquals");
        assertEquals(expected, readText(steps, locator), "Element text");
    }


    public static BigDecimal parseDollarAmount(String displayedText) {
        return MoneyAssertions.parseDollarAmount(displayedText);
    }

    public static void assertAmountsEqual(String message, BigDecimal expected, BigDecimal actual) {
        MoneyAssertions.assertAmountsEqual(message, expected, actual);
    }
}

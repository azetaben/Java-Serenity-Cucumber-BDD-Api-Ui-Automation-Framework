package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.pages.NavigationPage;
import saucedemo.com.support.FrameworkLogger;

import java.net.URI;
import java.util.List;

public class NavigationActions extends UIInteractionSteps {
    @Steps
    private NavigationPage navigationPage;
    @Steps
    private LoginPage loginPage;

    @Step("Open the Sauce Demo page")
    public void open_sauce_demo_page() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Open the Sauce Demo page");
        navigationPage.open();
    }

    @Step("Navigate to application path: {0}")
    public void navigate_to_application_path(String path) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Navigate to application path: " + path);
        getDriver().get(saucedemo.com.properties.SauceDemoConfig.absoluteUrlFor(path));
    }

    @Step("Open the menu button")
    public void openMenuButton() {
        FrameworkLogger.pageOperationStarted(NavigationPage.class, "openMenuButton");
        if (!isMenuOpen()) {
            $(navigationPage.getOpenMenuButton()).waitUntilClickable().click();
        }
        waitUntilMenuOpen();
    }

    public void waitUntilMenuOpen() {
        waitForCondition().until(driver -> isMenuOpen());
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
    }

    @Step("Open the burger menu")
    public void open_burger_menu() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Open the burger menu");
        openMenuButton();
    }

    private boolean isMenuOpen() {
        try {
            return getDriver().findElements(navigationPage.getMenuPanel()).stream().anyMatch(panel ->
                    panel.isDisplayed()
                            && panel.getAttribute("hidden") == null
                            && !"true".equalsIgnoreCase(panel.getAttribute("aria-hidden")));
        } catch (StaleElementReferenceException exception) {
            return false;
        }
    }

    @Step("Close the menu")
    public void close_menu() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Close the menu");
        if (isMenuOpen()) {
            $(navigationPage.getCloseMenuButton()).waitUntilClickable().click();
        }
        // Wait for the slide-out transition, not just the immediate aria-hidden change.
        waitForCondition().until(driver -> {
            try {
                return driver.findElements(navigationPage.getMenuPanel()).stream().noneMatch(element -> element.isDisplayed());
            } catch (StaleElementReferenceException exception) {
                return false;
            }
        });
    }


    @Step("Select burger menu option: {0}")
    public void select_burger_menu_option(String option) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Select burger menu option: " + option);
        ensureBurgerMenuOpen();
        $(navigationPage.menuLinkFor(option)).waitUntilClickable().click();
    }

    @Step("Select navigation option: {0}")
    public void select_navigation_option(String option) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Select navigation option: " + option);
        select_burger_menu_option(option);
    }

    @Step("Verify menu contains options: {0}")
    public void should_contain_menu_options(List<String> options) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify menu contains options: " + options);
        waitUntilMenuOpen();
        for (String option : options)
            $(navigationPage.menuLinkFor(option)).waitUntilVisible();
    }

    @Step("Verify menu close button is displayed")
    public void should_display_menu_close_button() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify menu close button is displayed");
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
    }

    @Step("Open the shopping cart")
    public void open_shopping_cart() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Open the shopping cart");
        $(navigationPage.getShoppingCartLink()).waitUntilClickable().click();
    }

    @Step("Verify cart has item count: {0}")
    public void should_have_cart_item_count(int expected) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify cart has item count: " + expected);
        var badge = $(navigationPage.getShoppingCartBadge());
        if (expected == 0) Assertions.assertFalse(badge.isCurrentlyVisible());
        else Assertions.assertEquals(Integer.toString(expected), badge.waitUntilVisible().getText().trim());
    }

    @Step("Verify cart badge is displayed")
    public void verify_cart_badge_is_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify cart badge is displayed");
        $(navigationPage.getShoppingCartBadge()).waitUntilVisible();
    }

    @Step("Verify cart badge is not displayed")
    public void verify_cart_badge_is_not_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify cart badge is not displayed");
        Assertions.assertFalse($(navigationPage.getShoppingCartBadge()).isCurrentlyVisible());
    }

    @Step("Verify current page header is: {0}")
    public void assert_current_page_header(String expected) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify current page header is: " + expected);
        Assertions.assertEquals(expected, $(navigationPage.getPageTitle()).waitUntilVisible().getText().trim());
    }

    @Step("Verify page title is: {0}")
    public void page_title_should_be(String title) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify page title is: " + title);
        Assertions.assertEquals(title, getDriver().getTitle());
    }

    @Step("Verify the logo is displayed")
    public void the_logo_should_be_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify the logo is displayed");
        String currentPath = java.net.URI.create(getDriver().getCurrentUrl()).getPath();
        By logo = currentPath == null || currentPath.isBlank() || currentPath.equals("/")
                || currentPath.equals("/index.html")
                ? loginPage.getLoginLogo()
                : navigationPage.getAppLogo();
        $(logo).waitUntilVisible();
    }

    @Step("Verify the current page is: {0}")
    public void verify_page(String name) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify the current page is: " + name);
        String path = PageUrls.pathFor(name);
        waitForCondition().until(d -> URI.create(d.getCurrentUrl()).getPath().equals(path));

    }

    @Step("Verify the page URL contains path: {0}")
    public void the_page_url_should_contain_pathUrl(String path) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify the page URL contains path: " + path);
        waitForCondition().until(d -> d.getCurrentUrl().contains(path));
    }

    @Step("Tap on the item menu link: {0}")
    public void tap_on_the_item_menu_link(String slug) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Tap on the item menu link: " + slug);
        ensureBurgerMenuOpen();
        $(navigationPage.tapOnMenuLinkFor(slug)).waitUntilClickable().click();
    }

    private void ensureBurgerMenuOpen() {
        openMenuButton();
    }

    public void i_should_see_in_the_shopping_cart_badge(int count) {
        FrameworkLogger.actionStarted(NavigationActions.class, "Verify the shopping cart badge count is: " + count);
        $(navigationPage.getCartBadgeCountFor(count)).waitUntilVisible();
        Assertions.assertEquals(Integer.toString(count), $(navigationPage.getCartBadgeCountFor(count)).getText().trim());
    }
}

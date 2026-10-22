package saucedemo.com.actions;

import saucedemo.com.support.FrameworkLogger;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.pages.NavigationPage;

import java.util.List;

public class NavigationActions extends UIInteractionSteps {
    @Steps
    private NavigationPage navigationPage;
    @Steps
    private LoginPage loginPage;

    @Step("Open the Sauce Demo page")
    public void open_sauce_demo_page() {
        FrameworkLogger.actionStarted(NavigationActions.class, "open_sauce_demo_page");
        navigationPage.open();
    }

    @Step("Navigate to application path: {0}")
    public void navigate_to_application_path(String path) {
        FrameworkLogger.actionStarted(NavigationActions.class, "navigate_to_application_path");
        getDriver().get(saucedemo.com.properties.SauceDemoConfig.absoluteUrlFor(path));
    }

    @Step("Open the burger menu")
    public void open_burger_menu() {
        FrameworkLogger.actionStarted(NavigationActions.class, "open_burger_menu");
        navigationPage.openMenuButton();
    }

    @Step("Close the menu")
    public void close_menu() {
        FrameworkLogger.actionStarted(NavigationActions.class, "close_menu");
        $(navigationPage.getCloseMenuButton()).waitUntilClickable().click();
    }

    @Step("Select burger menu option: {0}")
    public void select_burger_menu_option(String option) {
        FrameworkLogger.actionStarted(NavigationActions.class, "select_burger_menu_option");
        ensureBurgerMenuOpen();
        $(navigationPage.menuLinkFor(option)).waitUntilClickable().click();
    }

    @Step("Select navigation option: {0}")
    public void select_navigation_option(String option) {
        FrameworkLogger.actionStarted(NavigationActions.class, "select_navigation_option");
        select_burger_menu_option(option);
    }

    @Step("Verify menu contains options: {0}")
    public void should_contain_menu_options(List<String> options) {
        FrameworkLogger.actionStarted(NavigationActions.class, "should_contain_menu_options");
        $(navigationPage.getMenuPanel()).waitUntilVisible();
        for (String option : options)
            Assertions.assertTrue($(navigationPage.menuLinkFor(option)).isPresent(), "Missing menu option: " + option);
    }

    @Step("Verify menu close button is displayed")
    public void should_display_menu_close_button() {
        FrameworkLogger.actionStarted(NavigationActions.class, "should_display_menu_close_button");
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
    }

    @Step("Open the shopping cart")
    public void open_shopping_cart() {
        FrameworkLogger.actionStarted(NavigationActions.class, "open_shopping_cart");
        $(navigationPage.getShoppingCartLink()).waitUntilClickable().click();
    }

    @Step("Verify cart has item count: {0}")
    public void should_have_cart_item_count(int expected) {
        FrameworkLogger.actionStarted(NavigationActions.class, "should_have_cart_item_count");
        var badge = $(navigationPage.getShoppingCartBadge());
        if (expected == 0) Assertions.assertFalse(badge.isCurrentlyVisible());
        else Assertions.assertEquals(Integer.toString(expected), badge.waitUntilVisible().getText().trim());
    }

    @Step("Verify cart badge is displayed")
    public void verify_cart_badge_is_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "verify_cart_badge_is_displayed");
        $(navigationPage.getShoppingCartBadge()).waitUntilVisible();
    }

    @Step("Verify cart badge is not displayed")
    public void verify_cart_badge_is_not_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "verify_cart_badge_is_not_displayed");
        Assertions.assertFalse($(navigationPage.getShoppingCartBadge()).isCurrentlyVisible());
    }

    @Step("Verify current page header is: {0}")
    public void assert_current_page_header(String expected) {
        FrameworkLogger.actionStarted(NavigationActions.class, "assert_current_page_header");
        Assertions.assertEquals(expected, $(navigationPage.getPageTitle()).waitUntilVisible().getText().trim());
    }

    @Step("Verify page title is: {0}")
    public void page_title_should_be(String title) {
        FrameworkLogger.actionStarted(NavigationActions.class, "page_title_should_be");
        Assertions.assertEquals(title, getDriver().getTitle());
    }

    @Step("Verify the logo is displayed")
    public void the_logo_should_be_displayed() {
        FrameworkLogger.actionStarted(NavigationActions.class, "the_logo_should_be_displayed");
        String currentPath = java.net.URI.create(getDriver().getCurrentUrl()).getPath();
        By logo = currentPath == null || currentPath.isBlank() || currentPath.equals("/")
                || currentPath.equals("/index.html")
                ? loginPage.getLoginLogo()
                : navigationPage.getAppLogo();
        $(logo).waitUntilVisible();
    }

    @Step("Verify the current page is: {0}")
    public void verify_page(String name) {
        FrameworkLogger.actionStarted(NavigationActions.class, "verify_page");
        String path = PageUrls.pathFor(name);
        waitForCondition().until(d -> java.net.URI.create(d.getCurrentUrl()).getPath().equals(path));
    }

    @Step("Verify the page URL contains path: {0}")
    public void the_page_url_should_contain_pathUrl(String path) {
        FrameworkLogger.actionStarted(NavigationActions.class, "the_page_url_should_contain_pathUrl");
        waitForCondition().until(d -> d.getCurrentUrl().contains(path));
    }

    @Step("Tap on the item menu link: {0}")
    public void tap_on_the_item_menu_link(String slug) {
        FrameworkLogger.actionStarted(NavigationActions.class, "tap_on_the_item_menu_link");
        ensureBurgerMenuOpen();
        $(navigationPage.tapOnMenuLinkFor(slug)).waitUntilClickable().click();
    }

    private void ensureBurgerMenuOpen() {
        org.openqa.selenium.WebElement menu = getDriver().findElement(navigationPage.getMenuPanel());
        if (menu.getAttribute("hidden") != null || "true".equalsIgnoreCase(menu.getAttribute("aria-hidden"))) {
            open_burger_menu();
        }
        waitForCondition().until(driver -> {
            org.openqa.selenium.WebElement openMenu = driver.findElement(navigationPage.getMenuPanel());
            return openMenu.isDisplayed()
                    && openMenu.getAttribute("hidden") == null
                    && !"true".equalsIgnoreCase(openMenu.getAttribute("aria-hidden"));
        });
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
    }
}

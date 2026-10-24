package saucedemo.com.actions;

import java.net.URI;
import java.util.List;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.pages.LoginPage;
import saucedemo.com.pages.NavigationPage;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigationActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(NavigationActions.class);
    @Steps
    private NavigationPage navigationPage;
    @Steps
    private LoginPage loginPage;

    @Step("Open the Sauce Demo page")
    public void open_sauce_demo_page() {
        navigationPage.open();
        log.info("Opened the Sauce Demo page");
    }

    @Step("Navigate to application path: {0}")
    public void navigate_to_application_path(String path) {
        getDriver().get(saucedemo.com.properties.SauceDemoConfig.absoluteUrlFor(path));
        log.info("Navigated to application path: {}", path);
    }

    @Step("Open the menu button")
    public void openMenuButton() {
        if (!isMenuOpen()) {
            $(navigationPage.getOpenMenuButton()).waitUntilClickable().click();
        }
        waitUntilMenuOpen();
        log.info("Opened the menu button");
    }

    public void waitUntilMenuOpen() {
        waitForCondition().until(driver -> isMenuOpen());
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
        log.info("Verified the menu is open");
    }

    @Step("Open the burger menu")
    public void open_burger_menu() {
        openMenuButton();
    }

    private boolean isMenuOpen() {
        try {
            return getDriver().findElements(navigationPage.getMenuPanel()).stream().anyMatch(panel -> {
                    log.info("Checked if the menu is open");
                    return panel.isDisplayed()
                            && panel.getAttribute("hidden") == null
                            && !"true".equalsIgnoreCase(panel.getAttribute("aria-hidden"));
            });

        } catch (StaleElementReferenceException exception) {
            return false;
        }
    }

    @Step("Close the menu")
    public void close_menu() {
        if (isMenuOpen()) {
            $(navigationPage.getCloseMenuButton()).waitUntilClickable().click();
        }
        // Wait for the slide-out transition, not just the immediate aria-hidden change.
        waitForCondition().until(driver -> {
            try {
                return driver.findElements(navigationPage.getMenuPanel()).stream().noneMatch(WebElement::isDisplayed);
            } catch (StaleElementReferenceException exception) {
                return false;
            }
        });
        log.info("Verified the menu is closed");
    }


    @Step("Select burger menu option: {0}")
    public void select_burger_menu_option(String option) {
        ensureBurgerMenuOpen();
        $(navigationPage.menuLinkFor(option)).waitUntilClickable().click();
        log.info("Selected burger menu option: {}", option);
    }

    @Step("Select navigation option: {0}")
    public void select_navigation_option(String option) {
        select_burger_menu_option(option);
        log.info("Selected navigation option: {}", option);
    }

    @Step("Verify menu contains options: {0}")
    public void should_contain_menu_options(List<String> options) {
        waitUntilMenuOpen();
        for (String option : options)
            $(navigationPage.menuLinkFor(option)).waitUntilVisible();
        log.info("Verified menu contains options: {}", options);
    }

    @Step("Verify menu close button is displayed")
    public void should_display_menu_close_button() {
        $(navigationPage.getCloseMenuButton()).waitUntilVisible();
        log.info("Verified menu close button is displayed");
    }

    @Step("Open the shopping cart")
    public void open_shopping_cart() {
        $(navigationPage.getShoppingCartLink()).waitUntilClickable().click();
        log.info("Opened the shopping cart");
    }

    @Step("Verify cart has item count: {0}")
    public void should_have_cart_item_count(int expected) {
        var badge = $(navigationPage.getShoppingCartBadge());
        if (expected == 0) assertThat(badge.isCurrentlyVisible()).isFalse();
        else assertThat(badge.waitUntilVisible().getText().trim()).isEqualTo(Integer.toString(expected));
        log.info("Verified cart has item count: {}", expected);
    }

    @Step("Verify cart badge is displayed")
    public void verify_cart_badge_is_displayed() {
        $(navigationPage.getShoppingCartBadge()).waitUntilVisible();
        log.info("Verified cart badge is displayed");
    }

    @Step("Verify cart badge is not displayed")
    public void verify_cart_badge_is_not_displayed() {
        assertThat($(navigationPage.getShoppingCartBadge()).isCurrentlyVisible()).isFalse();
        log.info("Verified cart badge is not displayed");
    }

    @Step("Verify current page header is: {0}")
    public void assert_current_page_header(String expected) {
        log.info("Verify current page header is: {}", expected);
        assertThat($(navigationPage.getPageTitle()).waitUntilVisible().getText().trim()).isEqualTo(expected);
    }

    @Step("Verify page title is: {0}")
    public void page_title_should_be(String title) {
        log.info("Verify page title is: {}", title);
        assertThat(getDriver().getTitle()).isEqualTo(title);
        log.info("Verified page title is: {}", title);
    }

    @Step("Verify the logo is displayed")
    public void the_logo_should_be_displayed() {
        String currentPath = java.net.URI.create(getDriver().getCurrentUrl()).getPath();
        By logo = currentPath == null || currentPath.isBlank() || currentPath.equals("/")
                || currentPath.equals("/index.html")
                ? loginPage.getLoginLogo()
                : navigationPage.getAppLogo();
        $(logo).waitUntilVisible();
        log.info("Verified the logo is displayed");
    }

    @Step("Verify the current page URL is: {0}")
    public void verify_page_url(String name) {
        String path = PageUrls.pathFor(name);
        waitForCondition().until(d -> URI.create(d.getCurrentUrl()).getPath().equals(path));
        log.info("Verified the current page URL is: {}", name);

    }

    @Step("Verify the page URL contains path: {0}")
    public void verify_page_path_url(String path) {
        waitForCondition().until(d -> d.getCurrentUrl().contains(path));
        log.info("Verified the page URL contains path: {}", path);
    }

    @Step("Tap on the item menu link: {0}")
    public void tap_on_the_item_menu_link(String slug) {
        ensureBurgerMenuOpen();
        $(navigationPage.tapOnMenuLinkFor(slug)).waitUntilClickable().click();
        log.info("Tapped on the item menu link: {}", slug);
    }

    private void ensureBurgerMenuOpen() {
        openMenuButton();
        log.info("Verified the burger menu is open");
    }

    public void i_should_see_in_the_shopping_cart_badge(int count) {
        $(navigationPage.getCartBadgeCountFor(count)).waitUntilVisible();
        assertThat($(navigationPage.getCartBadgeCountFor(count)).getText().trim()).isEqualTo(Integer.toString(count));
        log.info("Verified the shopping cart badge count is: {}", count);
    }
}

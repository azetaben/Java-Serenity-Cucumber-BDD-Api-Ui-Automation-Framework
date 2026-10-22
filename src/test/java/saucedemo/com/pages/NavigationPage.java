package saucedemo.com.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.By;
import saucedemo.com.support.FrameworkLogger;

import java.util.Locale;

/**
 * Page locators; business interactions belong in the corresponding action class.
 */
public class NavigationPage extends PageObject {
    private final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private final By HEADER_CONTAINER = By.id("header_container");
    private final By PRIMARY_HEADER = By.cssSelector("[data-test='primary-header']");
    private final By SECONDARY_HEADER = By.cssSelector("[data-test='secondary-header']");
    private final By APP_LOGO = By.cssSelector("#header_container .app_logo");
    private final By OPEN_MENU_BUTTON = By.id("react-burger-menu-btn");
    private final By CLOSE_MENU_BUTTON = By.id("react-burger-cross-btn");
    private final By MENU_PANEL = By.cssSelector("#menu_button_container .bm-menu-wrap");
    private final By ALL_ITEMS_LINK = By.id("inventory_sidebar_link");
    private final By DYNAMIC_CATALOG_LINK = By.id("dynamic_catalog_sidebar_link");
    private final By ABOUT_LINK = By.id("about_sidebar_link");
    private final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private final By RESET_APP_STATE_LINK = By.id("reset_sidebar_link");
    private final By SHOPPING_CART_LINK = By.cssSelector("[data-test='cart-link']");
    private final By SHOPPING_CART_BADGE = By.cssSelector("[data-test='cart-badge']");
    private final By BACK_TO_PRODUCTS_BUTTON = By.id("back-to-products");

    public By getHeaderContainer() {
        return HEADER_CONTAINER;
    }

    public By getPrimaryHeader() {
        return PRIMARY_HEADER;
    }

    public By getSecondaryHeader() {
        return SECONDARY_HEADER;
    }

    public By getAppLogo() {
        return APP_LOGO;
    }

    public By getOpenMenuButton() {
        return OPEN_MENU_BUTTON;
    }

    public By getCloseMenuButton() {
        return CLOSE_MENU_BUTTON;
    }

    public By getMenuPanel() {
        return MENU_PANEL;
    }

    public By getAllItemsLink() {
        return ALL_ITEMS_LINK;
    }

    public By getDynamicCatalogLink() {
        return DYNAMIC_CATALOG_LINK;
    }

    public By getAboutLink() {
        return ABOUT_LINK;
    }

    public By getLogoutLink() {
        return LOGOUT_LINK;
    }

    public By getResetAppStateLink() {
        return RESET_APP_STATE_LINK;
    }

    public By getShoppingCartLink() {
        return SHOPPING_CART_LINK;
    }

    public By getShoppingCartBadge() {
        return SHOPPING_CART_BADGE;
    }

    public By getBackToProductsButton() {
        return BACK_TO_PRODUCTS_BUTTON;
    }

    public By menuLinkFor(String option) {
        String key = option.trim()
                .toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        switch (key) {
            case "inventory":
            case "all items":
                return ALL_ITEMS_LINK;
            case "dynamic catalog":
                return DYNAMIC_CATALOG_LINK;
            case "about":
                return ABOUT_LINK;
            case "logout":
                return LOGOUT_LINK;
            case "reset":
            case "reset app state":
                return RESET_APP_STATE_LINK;
            default:
                throw new IllegalArgumentException("Unknown navigation option: " + option);
        }
    }

    public By getPageTitle() {
        return PAGE_TITLE;
    }


    public By tapOnMenuLinkFor(String itemSlug) {
        return By.id(itemSlug + "_sidebar_link");
    }

    public void openMenuButton() {
        FrameworkLogger.pageOperationStarted(NavigationPage.class, "openMenuButton");
        $(OPEN_MENU_BUTTON).waitUntilClickable().click();
    }

}

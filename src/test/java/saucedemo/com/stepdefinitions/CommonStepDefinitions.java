package saucedemo.com.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.LoginActions;
import saucedemo.com.actions.NavigationActions;
import saucedemo.com.actions.ScenarioSetupActions;
import saucedemo.com.actions.UiWorkflowActions;
import saucedemo.com.contentdesign.PageUrls;
import saucedemo.com.support.FrameworkLogger;
import saucedemo.com.support.GooglePasswordManager;

import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;

/**
 * Shared page setup, navigation and assertions used across UI features.
 */
public class CommonStepDefinitions {
    @Steps
    private ScenarioSetupActions setup;
    @Steps
    private LoginActions loginActions;
    @Steps
    private NavigationActions navigationActions;
    @Steps
    private UiWorkflowActions workflow;

    @Given("I prepare the {string} page")
    public void iPrepareThePage(String page) throws InterruptedException {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "iPrepareThePage");
        setup.preparePage(page);
    }

    @Given("I am on the {string} page")
    public void i_am_on_the_specific_page(String page) throws InterruptedException {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_am_on_the_specific_page");
        setup.preparePage(page);

    }

    @Then("I should be redirected to {string} page")
    public void iShouldBeRedirectedToPage(String page) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "iShouldBeRedirectedToPage");
        navigationActions.verify_page(page);
        if (PageUrls.pathFor(page).equals(PageUrls.pathFor("inventory"))) {
            GooglePasswordManager.dismissIfVisible(
                    getDriver());
        }
    }

    @Then("I should see the page header as {string}")
    public void i_should_see_the_page_header_as(String pageHeader) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_should_see_the_page_header_as");
        navigationActions.assert_current_page_header(pageHeader);
    }

    @Then("I should see the page title {string}")
    public void i_should_see_the_page_title(String pageTitle) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_should_see_the_page_title");
        workflow.assert_page_title(pageTitle);
    }

    @When("I tap on the menu button")
    public void i_tap_on_the_menu_button() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_tap_on_the_menu_button");
        navigationActions.open_burger_menu();

    }

    @When("I select the {string} menu option")
    public void i_select_the_menu_option(String option) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_select_the_menu_option");
        navigationActions.select_navigation_option(option);
    }

    @When("I navigate to the shopping cart")
    @When("I open the cart")
    public void i_open_the_cart() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_open_the_cart");
        navigationActions.open_shopping_cart();
    }

    @When("I return to the inventory")
    public void i_return_to_the_inventory() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_return_to_the_inventory");
        workflow.return_to_inventory();
    }

    @Then("the cart badge should show {string}")
    public void the_cart_badge_should_show(String count) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_cart_badge_should_show");
        navigationActions.should_have_cart_item_count(Integer.parseInt(count));
    }

    @Then("the cart badge should not be displayed")
    public void the_cart_badge_should_not_be_displayed() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_cart_badge_should_not_be_displayed");
        navigationActions.should_have_cart_item_count(0);
    }

    @And("I tap on the {string} menu link")
    public void i_tap_on_the_menu_link(String itemSlug) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_tap_on_the_menu_link");
        navigationActions.tap_on_the_item_menu_link(itemSlug);
    }

    @And("the shopping cart badge should not be visible")
    public void the_shopping_cart_badge_should_not_be_visible() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_shopping_cart_badge_should_not_be_visible");
        navigationActions.verify_cart_badge_is_not_displayed();
    }

    @And("the shopping cart badge should be visible")
    public void the_shopping_cart_badge_should_be_visible() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_shopping_cart_badge_should_be_visible");
        navigationActions.verify_cart_badge_is_displayed();
    }

    @And("I can see the page header as {string}")
    public void i_can_see_the_page_header_as(String pageHeader) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_can_see_the_page_header_as");
        navigationActions.assert_current_page_header(pageHeader);
    }

    @Given("I open the browser on the SauceDemo page")
    public void i_open_the_browser_on_the_saucedemo_page() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_open_the_browser_on_the_saucedemo_page");
        navigationActions.open_sauce_demo_page();
    }

    @Then("I should be redirected to the URL {string}")
    public void i_should_be_redirected_to_the_url(String url) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_should_be_redirected_to_the_url");
        loginActions.verify_url(url);

    }

    @Then("I should see the error message {string}")
    public void i_should_see_the_error_message(String message) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_should_see_the_error_message");
        loginActions.verify_error_msg(message);

    }

    @When("I close the error message")
    public void i_close_the_error_message() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_close_the_error_message");
        loginActions.dismiss_error();
    }

    @When("I cancel checkout")
    public void i_cancel_checkout() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_cancel_checkout");
        workflow.cancel_checkout();
    }

    @Then("the page URL path should be {string}")
    public void the_page_url_path_should_be(String pathUrl) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_page_url_path_should_be");
        navigationActions.the_page_url_should_contain_pathUrl(pathUrl);

    }

    @And("the page title should be {string}")
    public void the_page_title_should_be(String pageTitle) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_page_title_should_be");
        navigationActions.page_title_should_be(pageTitle);
    }

    @And("the logo should be displayed")
    public void the_logo_should_be_displayed() {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "the_logo_should_be_displayed");
        navigationActions.the_logo_should_be_displayed();

    }

    @Then("I should be on the {string} page")
    public void i_should_be_on_the_page(String pageName) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "i_should_be_on_the_page");
        navigationActions.verify_page(pageName);
    }

    @Then("I should be redirected to the {string} page")
    public void iShouldBeRedirectedToThePage(String pageUrlPath) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "iShouldBeRedirectedToThePage");
        navigationActions.verify_page(pageUrlPath);
    }

    @Then("I should see {int} in the shopping cart badge")
    public void iShouldSeeInTheShoppingCartBadge(int count) {
        FrameworkLogger.stepDefinitionStarted(CommonStepDefinitions.class, "iShouldSeeInTheShoppingCartBadge");
        navigationActions.i_should_see_in_the_shopping_cart_badge(count);
    }
}

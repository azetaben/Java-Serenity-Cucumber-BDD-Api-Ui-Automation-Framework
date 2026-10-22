package saucedemo.com.stepdefinitions;

import saucedemo.com.support.FrameworkLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import saucedemo.com.actions.NavigationActions;

/**
 * Burger-menu phrases only; shared page, login and badge bindings remain in existing classes.
 */
public class BurgerMenuStepDefinitions {
    @Steps
    private NavigationActions navigationActions;

    @Given("I navigate to {string}")
    public void i_navigate_to(String path) {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "i_navigate_to");
        navigationActions.navigate_to_application_path(path);

    }

    @When("I open the burger menu")
    public void i_open_the_burger_menu() {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "i_open_the_burger_menu");
        navigationActions.open_burger_menu();

    }

    @Then("the burger menu list should contain:")
    public void the_burger_menu_list_should_contain(DataTable table) {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "the_burger_menu_list_should_contain");
        navigationActions.should_contain_menu_options(table.asList(String.class));
    }

    @Then("the burger menu close button is displayed")
    public void the_burger_menu_close_button_is_displayed() {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "the_burger_menu_close_button_is_displayed");
        navigationActions.should_display_menu_close_button();

    }

    @When("I close the burger menu")
    public void i_close_the_burger_menu() {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "i_close_the_burger_menu");
        navigationActions.close_menu();

    }

    @When("I select {string} from the burger menu list")
    @When("I tap burger menu and select {string} from the burger menu list")
    public void i_tap_burger_menu_and_select_from_the_burger_menu_list(String option) {
        FrameworkLogger.stepDefinitionStarted(BurgerMenuStepDefinitions.class, "i_tap_burger_menu_and_select_from_the_burger_menu_list");
        navigationActions.select_burger_menu_option(option);

    }
}

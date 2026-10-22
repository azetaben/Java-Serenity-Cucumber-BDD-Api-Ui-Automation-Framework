@Demo @UI
Feature: Sauce Demo Shopping Cart and Logout

  Scenario: Add, remove a product and logout
    Given I open the browser on the SauceDemo page
    And I am logged into Sauce Demo with user "standard_user"
    When I add the item "sauce-labs-backpack" to the cart
    Then the cart badge should show "1"
    And Remove button is displayed for "sauce-labs-backpack"
    And I remove "sauce-labs-backpack" from the cart
    Then the cart badge should not be displayed
    When I open the cart
    Then I should be redirected to "cart" page
    And the cart should be empty
    When I tap on the menu button
    And I select the "logout" menu option
    Then I should be redirected to "login" page

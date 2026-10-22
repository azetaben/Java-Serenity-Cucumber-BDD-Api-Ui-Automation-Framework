@UI
Feature: logout and end to end test for SauceDemo application

  Background:
    Given I open the browser on the SauceDemo page
    Then I should be redirected to "login" page
    When I login as accepted user
    Then I should be redirected to "inventory" page

  @logout
  Scenario: 01 - logout from inventory page
    And I select the "Logout" menu option
    Then I should be redirected to "login" page

  Scenario: 02 - logout from product details page
    And I open product "Sauce Labs Backpack"
    Then I should be redirected to "product details" page
    And I select the "Logout" menu option
    Then I should be redirected to "login" page

  Scenario: 03 - logout from cart page
    And I open product "Sauce Labs Backpack"
    Then I should be redirected to "product details" page
    And I open the cart
    Then I should be redirected to "cart" page
    And I select the "Logout" menu option
    Then I should be redirected to "login" page

  Scenario: 04 - logout from checkout information page
    And I open product "Sauce Labs Backpack"
    Then I should be redirected to "product details" page
    And I open the cart
    And I proceed to checkout
    Then I should be redirected to "checkout information" page
    And I select the "Logout" menu option
    Then I should be redirected to "login" page

  Scenario Outline: 04 - logout from specific <page>
    And I prepare the "<page>" page
#    And I am on the "<page>" page
    Then I should be redirected to "<page>" page
#    And I select the "Logout" menu option
    And I tap on the "logout" menu link
    Then I should be redirected to "login" page
    Examples:
      | page                 |
      | inventory            |
      | inventory item       |
      | cart                 |
      | checkout information |

  Scenario Outline: 05- logout from <page>
    Given I prepare the "<page>" page
    When I select the "logout" menu option
    Then I should be redirected to "login" page

    Examples:
      | page                 |
      | inventory            |
      | inventory item       |
      | cart                 |
      | checkout information |
      | checkout overview    |
      | checkout complete    |

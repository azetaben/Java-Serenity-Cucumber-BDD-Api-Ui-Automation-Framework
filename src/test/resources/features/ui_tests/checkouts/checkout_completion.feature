@UI
Feature: Checkout completion

  Background:
    Given I open the browser on the SauceDemo page
    Then I should be redirected to "login" page
    When I login as accepted user
    Then I should be redirected to "inventory" page
    And I am on the inventory page
    And I should see the login result "success"
    And I add product "Sauce Labs Backpack" and verify its button changes to Remove
    And I add product "Sauce Labs Onesie" and verify its button changes to Remove
    And I open the cart
    Then I should be redirected to "cart" page
    And I proceed to checkout
    Then I should be redirected to "checkout information" page
    And I submit checkout information
    Then I should be redirected to "checkout overview" page
    And I finish checkout
    Then I should be redirected to "checkout complete" page

  Scenario: Verify order confirmation and return to the inventory
    Then the Pony Express icon is visible
    And I should see the checkout confirmation "Thank you for your order!"
    And the order dispatch message is visible
    And the Back Home button is displayed
    When I return to the inventory
    Then I should be redirected to "inventory" page

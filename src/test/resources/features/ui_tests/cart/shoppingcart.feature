@Demo @UI
Feature: Shopping cart

  Background:
    Given I open the browser on the SauceDemo page
    Then I should be redirected to "login" page
    When I login as accepted user
    Then I should be redirected to "inventory" page
    And I am on the inventory page
    And I should see the login result "success"
    And I add product "Sauce Labs Backpack" and verify its button changes to Remove
    And I add product "Sauce Labs Onesie" and verify its button changes to Remove
    And I add product "Sauce Labs Bolt T-Shirt" and verify its button changes to Remove
    And I add product "Sauce Labs Fleece Jacket" and verify its button changes to Remove
    And I add product "Sauce Labs Bike Light" and verify its button changes to Remove
    And I open the cart
    Then I should be redirected to "cart" page

  Scenario: Verify cart contents and buttons
    Then the cart contains 5 products
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Backpack      |
      | Sauce Labs Onesie        |
      | Sauce Labs Bolt T-Shirt  |
      | Sauce Labs Fleece Jacket |
      | Sauce Labs Bike Light    |
    And Continue Shopping is visible and enabled
    And Checkout is visible and enabled

  Scenario Outline: Verify product price, quantity and Remove button
    Then the cart price of "<title>" is "<price>"
    And the cart quantity of "<title>" is 1
    And the cart Remove button for "<title>" is visible

    Examples:
      | title                    | price  |
      | Sauce Labs Backpack      | $29.99 |
      | Sauce Labs Onesie        | $7.99  |
      | Sauce Labs Bolt T-Shirt  | $15.99 |
      | Sauce Labs Fleece Jacket | $49.99 |
      | Sauce Labs Bike Light    | $9.99  |

  Scenario: Remove a product and verify the badge decreases
    When I remove cart product "Sauce Labs Backpack" and verify the badge decreases
    Then the cart contains 4 products

  Scenario: Proceed to checkout
    Then Checkout is visible and enabled
    When I proceed to checkout

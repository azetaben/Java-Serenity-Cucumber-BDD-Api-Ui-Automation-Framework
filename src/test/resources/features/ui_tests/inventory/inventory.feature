@UI
Feature: Inventory products

  Background:
    Given I open the browser on the SauceDemo page
    Then I should be redirected to "login" page
    When I login as accepted user
    Then I should be redirected to "inventory" page

  @ProductionSafe
  Scenario: Verify the product titles
    Then the inventory contains these product titles:
      | Sauce Labs Backpack               |
      | Sauce Labs Bike Light             |
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Onesie                 |
      | Test.allTheThings() T-Shirt (Red) |
    And the product title count is 6

  @ProductionSafe
  Scenario Outline: Verify product prices and images
    Then the price of product "<title>" is "<price>"
    And the image for product "<title>" is visible

    Examples:
      | title                             | price  |
      | Sauce Labs Backpack               | $29.99 |
      | Sauce Labs Bike Light             | $9.99  |
      | Sauce Labs Bolt T-Shirt           | $15.99 |
      | Sauce Labs Fleece Jacket          | $49.99 |
      | Sauce Labs Onesie                 | $7.99  |
      | Test.allTheThings() T-Shirt (Red) | $15.99 |

  Scenario: Add and remove a product
    Then the Add to cart button for product "Sauce Labs Bike Light" is visible
    When I add product "Sauce Labs Bike Light" and verify its button changes to Remove
    And I remove product "Sauce Labs Bike Light" and verify its button changes to Add to cart

  @ProductionSafe
  Scenario: Open a product
    When I open product "Sauce Labs Backpack"
    Then I should be redirected to "product details" page

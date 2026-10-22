@UI @ProductData @Positive
Feature: Add and remove catalog products
  As a customer
  I want to manage products in my cart
  So that I can choose what to purchase

  Background:
    Given I prepare the "inventory" page

  Scenario Outline: Add and remove a catalog product
    When I add product "<product>" to the cart
    Then I should see "Remove" button for "<product>"
    And the cart badge should show "1"

    When I remove "<product>" from the cart
    Then I should see "Add to cart" button for "<product>"
    And the cart badge should not be displayed

    Examples:
      | product       |
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |

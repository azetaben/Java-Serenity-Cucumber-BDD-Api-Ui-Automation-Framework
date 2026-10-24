Feature: Add to cart

  @smoke
  Scenario Outline: Add one quantity from Store
    Given I prepare the "login" page
    When I login as an accepted "standard" user
    Then I am on the inventory page
    When I add a "<product_name>" to the cart
    And I open the cart
    Then I should see 1 "<product_name>" in the cart
    Examples:
      | product_name        |
      | Sauce Labs Backpack |
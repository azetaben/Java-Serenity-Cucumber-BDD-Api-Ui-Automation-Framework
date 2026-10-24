@AUT @API @Login
Feature: API-preloaded login sessions
  The app has no login API,
  so scenarios check the service over HTTP and
  preload its client-side session cookie.

  Scenario Outline: Open the product catalog using an API-created session
    Given I login in login as <user>
    Then I should be redirected to "inventory" page

    Examples:
      | user            |
      | "standard_user" |
#      | "problem_user"            |
#      | "performance_glitch_user" |
#      | "error_user"              |
#      | "visual_user"             |


  Scenario: Open the shopping cart catalog with 5 items using an API-created session
    Given I have 5 items in the shopping cart
    When I navigate to the shopping cart
    Then I should be redirected to "cart" page
    And I should see 5 in the shopping cart badge
    And the cart contains 5 products
    When I remove cart product "Sauce Labs Backpack" and verify the badge decreases
    And I should see items details in the shopping cart
      | Product Title            | Quantity | $Price |
      | Sauce Labs Fleece Jacket | 1        | $49.99 |
      | Sauce Labs Bike Light    | 1        | $9.99  |
      | Sauce Labs Bolt T-Shirt  | 1        | $15.99 |
      | Sauce Labs Bike Light    | 1        | $9.99  |
    Then the cart contains 4 products
    When I proceed to checkout
    Then I should be redirected to "checkout information" page


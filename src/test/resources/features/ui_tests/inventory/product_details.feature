@UI
Feature: Product details

  Scenario: Verify product details and cart button transitions
    Given I open the browser on the SauceDemo page
    Then I should be redirected to "login" page
    When I login as accepted user
    Then I should be redirected to "inventory" page
    When I open product "Sauce Labs Backpack"
    Then the product title should be "Sauce Labs Backpack"
    And the price for "Sauce Labs Backpack" should be "$29.99"
    And the product image for "Sauce Labs Backpack" should be visible
    And the Add to cart button for "Sauce Labs Backpack" should be visible

    And I can see the following cart product details
      | Product Name        | Price |
      | Sauce Labs Backpack | 29.99 |

    Then I should see item details in the cart
      | Product Title       | Quantity | $Price |
      | Sauce Labs Backpack | 1        | 29.99  |

    And I should see "Sauce Labs Backpack" item in the cart
      | Product Name        | $Price |
      | Sauce Labs Backpack | 29.99  |

    When I add "Sauce Labs Backpack" to the cart
    Then the Remove button for "Sauce Labs Backpack" should be visible

    When I remove "Sauce Labs Backpack"
    Then the Add to cart button for "Sauce Labs Backpack" should be visible

    When I return to the inventory
    Then I should be redirected to "inventory" page
    And I select "Logout" from the burger menu list

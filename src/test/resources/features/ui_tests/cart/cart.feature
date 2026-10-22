@UI @AddToCart_1
Feature: Shopping cart

  Scenario Outline: Add a product to the cart
    Given I prepare the "inventory" page
    When I add product "<item>" to the cart
    Then the cart badge should show "1"

    Examples:
      | item                     |
      | Sauce Labs Backpack      |
      | Sauce Labs Onesie        |
      | Sauce Labs Bolt T-Shirt  |
      | Sauce Labs Fleece Jacket |
      | Sauce Labs Bike Light    |

  @UI @Regression @Cart @CartPrice
  Scenario: Display selected cart product details
    Given I prepare the "login" page
    When I login as an accepted "standard" user
#    When I login as an accepted user
    Then I should be redirected to "inventory" page

    When I add product items to the cart:
      | Sauce Labs Fleece Jacket |
      | Sauce Labs Onesie        |

    When I add multiple items to the cart:
      | Test.allTheThings() T-Shirt (Red) |
      | Sauce Labs Bike Light             |

    Then the cart badge should show "4"
    When I open the cart
    Then I should be redirected to "cart" page
    And the cart contains 4 products
    And I should see the following cart product details
      | name                              | quantity | price  |
      | Sauce Labs Fleece Jacket          | 1        | $49.99 |
      | Sauce Labs Bike Light             | 1        | $9.99  |
      | Test.allTheThings() T-Shirt (Red) | 1        | $15.99 |
      | Sauce Labs Bike Light             | 1        | $9.99  |


  Scenario: Add products to the cart
    Given I prepare the "inventory" page
    When I add product items to the cart:
      | Sauce Labs Backpack               |
      | Sauce Labs Onesie                 |
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Bike Light             |
      | Test.allTheThings() T-Shirt (Red) |
    Then the cart badge should show "6"
    When I navigate to the shopping cart
    Then I should be redirected to "cart" page
    And the cart contains 6 products
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Backpack               |
      | Sauce Labs Onesie                 |
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Bike Light             |
      | Test.allTheThings() T-Shirt (Red) |

  @AddToCart_1
  Scenario: Add a product item to the cart
    Given I prepare the "login" page
    And the page title should be "Swag Labs"
    And the logo should be displayed
    When I login as an accepted "standard" user
    And I should be redirected to "inventory" page
    Then I should be on the "products" page
    Then the page URL path should be "/inventory.html"
    And the page title should be "Swag Labs"
    And I should see the page header as "Products"
    And the shopping cart badge should not be visible
    When I add "backpack" items to the cart
    When I add product items to the cart:
      | Sauce Labs Onesie        |
      | Sauce Labs Fleece Jacket |
    And I should see "Remove" button for "backpack"
    Then the cart badge should show "3"
    And I navigate to the shopping cart
    Then I should be on the "Your Cart" page
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Backpack      |
      | Sauce Labs Onesie        |
      | Sauce Labs Fleece Jacket |
    And I continue shopping
    Then I should be on the "products" page
    And I can see the page header as "Products"
    And I should see "Remove" button for "backpack"
    When I remove "backpack" items from the cart
    And I should see "Add to cart" button for "backpack"
    When I add "backpack" items to the cart
    When I add product items to the cart:
      | Sauce Labs Bolt T-Shirt           |
      | Test.allTheThings() T-Shirt (Red) |
    And the shopping cart badge should be visible
    Then the cart badge should show "5"
    And I should see "Remove" button for "backpack"
    When I remove "backpack" items from the cart
    Then the cart badge should show "4"
    When I open the cart
    Then I should be on the "Your Cart" page
    Then I should be redirected to "cart" page
    And the page URL path should be "/cart.html"
    And I should see the page header as "Your Cart"
    And I can see the page header as "Your Cart"
    Then the cart badge should show "4"
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Onesie                 |
      | Test.allTheThings() T-Shirt (Red) |
    When I proceed to checkout
    Then I should be redirected to "checkout information" page
#    When I enter checkout information below:
#      | firstName | lastName | postalCode |
#      | John      | Doe      | M2 5JJ     |
    And I submit checkout information
    Then I should be redirected to "checkout overview" page
    And I finish checkout
    Then I should be redirected to "checkout complete" page
    And I tap on the "logout" menu link
    Then I should be redirected to "login" page

@Demo @UI
Feature: End to end test for SauceDemo application
  As a user,
  I want to be able to perform an end-to-end test for all users
  from login to checkout, including product selection and cart management.


  @Special_Regression
  Scenario Outline: end to end test for all users
    # LOGIN/ HOME PAGE
    Given I prepare the "login" page
    And the page title should be "Swag Labs"
    And the logo should be displayed
    Then I should be redirected to "login" page
    And I should see the login form
    And login form should contain the following fields:
      | Username field |
      | Password field |
      | Login button   |
    And accepted users credentials should be visible in the login form:
      | Username field          |
      | standard_user           |
      | locked_out_user         |
      | problem_user            |
      | performance_glitch_user |

    When I login as an accepted "<user>" user
    # INVENTORY PAGE
    And I should be redirected to "inventory" page
    Then I should be on the "products" page
    And I should see the page header as "Products"
    And the shopping cart badge should not be visible
    When I sort the inventory by "price low to high"
    Then the first inventory item should be "Sauce Labs Onesie" priced at "$7.99"
    When I open product "Sauce Labs Backpack"
    # PRODUCT DETAILS/ INVENTORY ITEM PAGE
    And I should be redirected to "inventory item" page
    When I add details product "Sauce Labs Backpack" and verify the badge increases
    Then the cart badge should show "1"
    And I remove "Sauce Labs Backpack" from the cart
    Then the cart badge should not be displayed
    When I return to the inventory
    # INVENTORY PAGE
    Then I should be redirected to "inventory" page
    When I add "backpack" items to the cart
    When I add product items to the cart:
      | Sauce Labs Onesie        |
      | Sauce Labs Fleece Jacket |
    And I should see "Remove" button for "backpack"
    Then the cart badge should show "3"
    And I navigate to the shopping cart
    # SHOPPING CART PAGE
    Then I should be on the "your cart" page
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Backpack      |
      | Sauce Labs Onesie        |
      | Sauce Labs Fleece Jacket |

    And I should see the following cart product details
      | name                     | quantity | price  |
      | Sauce Labs Fleece Jacket | 1        | $49.99 |
      | Sauce Labs Onesie        | 1        | $7.99  |

    And I continue shopping
    # INVENTORY PAGE
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
    When I navigate to the shopping cart
    # SHOPPING CART PAGE
    Then I should be on the "your cart" page
    Then the cart badge should show "4"
    And these cart product titles are displayed with Remove buttons:
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Fleece Jacket          |
      | Sauce Labs Onesie                 |
      | Test.allTheThings() T-Shirt (Red) |
    When I proceed to checkout
    # CHECKOUT INFORMATION PAGE
    Then I should be redirected to "checkout information" page
    When I enter checkout information below:
      | firstName | lastName | postalCode |
      | John      | Doe      | M2 5JJ     |

    And I submit checkout information

    # CHECKOUT OVERVIEW PAGE
    Then I should be redirected to "checkout overview" page

    Then the order summary should contain exactly these products:
      | product                           | quantity | unitPrice |
      | Sauce Labs Bolt T-Shirt           | 1        | $15.99    |
      | Sauce Labs Fleece Jacket          | 1        | $49.99    |
      | Sauce Labs Onesie                 | 1        | $7.99     |
      | Test.allTheThings() T-Shirt (Red) | 1        | $15.99    |

    And the item subtotal should be "89.96"
    And the item subtotal should equal the sum of product prices multiplied by quantities
    And the displayed tax should equal the configured tax rate applied to the item subtotal
    And the displayed total should equal the item subtotal plus tax
    And I finish checkout
    # CHECKOUT COMPLETE PAGE
    Then I should be redirected to "checkout complete" page
    Then the Pony Express icon is visible
    And I should see the checkout confirmation "Thank you for your order!"
    And the order dispatch message is visible
    When I generate and download the order PDF into downloadfiles
    And the Back Home button is displayed
    When I return to the inventory
    # NAVIGATION PAGE
    When I open the burger menu
    Then the burger menu list should contain:
      | All Items       |
      | About           |
      | Logout          |
      | Reset App State |
    And the burger menu close button is displayed
    And I close the burger menu
    And I select the "Logout" menu option
    Then I should be redirected to "login" page
    And I should see the login form

    Examples:
      | user     |
      | standard |
#      | problem_user            |
#      | performance_glitch_user |
#      | error_user              |
#      | visual_user             |

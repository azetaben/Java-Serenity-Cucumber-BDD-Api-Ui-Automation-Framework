@UI
Feature: Checkout
  As a customer, I want to complete checkout for the products in my cart.

  @Smoke @Regression @Checkout
  Scenario: Customer completes checkout for one product
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "STANDARD_USER"
    Then I am on the inventory page

    When I add the item "Sauce Labs Backpack" to the cart
    Then the cart badge should show "1"

    When I open the cart
    Then the cart should contain only "Sauce Labs Backpack"

    When I proceed to checkout
    When I enter checkout information below:
      | firstName | lastName | postalCode |
      | Alex      | Taylor   | SW1A 1AA   |
    And I continue from checkout information

    Then the order summary should contain exactly these products:
      | product             | quantity | unitPrice |
      | Sauce Labs Backpack | 1        | $29.99    |
    And the item subtotal should be "29.99"
    And the item subtotal should equal the sum of product prices multiplied by quantities
    And the displayed tax should equal the configured tax rate applied to the item subtotal
    And the displayed total should equal the item subtotal plus tax

    When I finish checkout
    Then I should see the checkout confirmation "Thank you for your order!"
    And I should see the order completion message:
      """
      Your order has been dispatched, and will arrive just as fast as the pony can get there!
      """

  @UI1 @Checkout @Positive
  Scenario Outline: Checkout information accepts valid customer details
    Given I open the browser on the SauceDemo page
    When I log in as "<username>" using the "<passwordMode>" password
    Then I am on the inventory page
    When I add the item "Sauce Labs Backpack" to the cart
    And I open the cart
    And I proceed to checkout
    Then I should see the checkout information page
    When I enter checkout information below:
      | firstName   | lastName   | postalCode   |
      | <firstName> | <lastName> | <postalCode> |
    And I continue from checkout information
    Then I should be redirected to the URL "https://www.saucedemo.com/checkout-step-two.html"

    Examples:
      | username      | passwordMode | firstName | lastName | postalCode |
      | standard_user | valid        | Alex      | Taylor   | SW1A 1AA   |

  @UI2 @Checkout @Negative
  Scenario Outline: Checkout information validates required fields
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "STANDARD_USER"
    Then I am on the inventory page
    When I add the item "Sauce Labs Backpack" to the cart
    And I open the cart
    And I proceed to checkout
    Then I should see the checkout information page
    When I enter checkout information below:
      | firstName   | lastName   | postalCode   |
      | <firstName> | <lastName> | <postalCode> |
    And I continue from checkout information
    Then I should see checkout information error "<errorMessage>"

    Examples:
      | firstName | lastName | postalCode | errorMessage                   |
      |           | Taylor   | SW1A 1AA   | Error: First Name is required  |
      | Alex      |          | SW1A 1AA   | Error: Last Name is required   |
      | Alex      | Taylor   |            | Error: Postal Code is required |

  @UI3 @Checkout
  Scenario: Cancelling checkout information returns to the cart
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "STANDARD_USER"
    Then I am on the inventory page
    When I add the item "Sauce Labs Backpack" to the cart
    And I open the cart
    And I proceed to checkout
    Then I should see the page title "Checkout: Your Information"
    When I cancel checkout
    Then I should see the page title "Your Cart"
    And I should see the product "Sauce Labs Backpack" listed

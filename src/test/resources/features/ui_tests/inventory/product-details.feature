@UI @ProductDetails @Positive
Feature: Should be able to navigate to product details page,
  add and remove products from the cart,
  and logout

  Scenario: Add, remove a product and logout
    Given I open the browser on the SauceDemo page
    And I am logged into Sauce Demo with user "standard_user"
    When I open product "Sauce Labs Backpack"
    Then I should be redirected to "product details" page
    When I add "Sauce Labs Backpack" to the cart
    And I remove "Sauce Labs Backpack" item from the cart
    And I tap on the back to products button
    Then I should be redirected to "inventory" page
    When I select "logout" from the burger menu list
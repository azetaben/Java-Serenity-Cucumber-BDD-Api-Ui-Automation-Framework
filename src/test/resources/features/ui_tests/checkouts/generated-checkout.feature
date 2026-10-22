@Demo @UI @GeneratedData @GeneratedCheckout
Feature: Checkout information validation with generated test data
  Customers can continue with non-empty information and receive required-field errors otherwise.

  Background:
    Given I generate test data using seed 20261016
    And I prepare the "checkout information" page

  @Positive
  Scenario Outline: Continue checkout with generated customer information
    When I enter generated "<case>" checkout information
    And I continue from checkout information
    Then I should be redirected to "checkout overview" page

    Examples:
      | case         |
      | valid        |
      | hyphenated   |
      | apostrophe   |
      | leading_zero |

  @Negative
  Scenario Outline: Reject missing generated checkout information in validation order
    When I enter generated "<case>" checkout information
    And I continue from checkout information
    Then the checkout validation should require "<field>"
    And I should be redirected to "checkout information" page

    Examples:
      | case                               | field       |
      | missing_first_name                 | first name  |
      | missing_last_name                  | last name   |
      | missing_postal_code                | postal code |
      | missing_first_and_last_name        | first name  |
      | missing_first_name_and_postal_code | first name  |
      | missing_last_name_and_postal_code  | last name   |
      | empty                              | first name  |

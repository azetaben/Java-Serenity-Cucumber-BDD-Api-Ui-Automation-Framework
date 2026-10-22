@UI @CheckoutData
Feature: Checkout information datasets

  Scenario Outline: Checkout information dataset <case> produces its expected result
    Given I prepare the "checkout information" page
    When I submit checkout information using "<case>"
    Then the checkout information result should match "<case>"

    Examples:
      | case                               |
      | VALID_CUSTOMER                     |
      | VALID_HYPHENATED_NAME              |
      | VALID_APOSTROPHE_NAME              |
      | VALID_UNICODE_NAME                 |
      | VALID_NUMERIC_POSTAL_CODE          |
      | VALID_LEADING_ZERO_POSTAL_CODE     |
      | MISSING_FIRST_NAME                 |
      | MISSING_LAST_NAME                  |
      | MISSING_POSTAL_CODE                |
      | MISSING_FIRST_AND_LAST_NAME        |
      | MISSING_FIRST_NAME_AND_POSTAL_CODE |
      | MISSING_LAST_NAME_AND_POSTAL_CODE  |
      | ALL_FIELDS_EMPTY                   |

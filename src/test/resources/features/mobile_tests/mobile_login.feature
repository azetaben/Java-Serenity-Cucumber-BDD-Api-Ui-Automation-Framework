@Mobile @MobileWeb @UI
Feature: SauceDemo on a mobile browser
  Scenario: Standard user logs in on a mobile browser
    Given I prepare the "login" page
    When I log in as "standard_user" using the "valid" password
    Then I should be on the "products" page
    And I should see the page header as "Products"
    And the Appium session is active

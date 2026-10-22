@Demo @UI @Login
Feature: Login
  Login scenarios use named password modes so credential values stay out of Gherkin and Serenity step text.

  Background:
    Given I open the browser on the SauceDemo page

  @Smoke @Regression
  Scenario: Login page exposes its form and advertised demo accounts
    Then I should be redirected to "login" page
    And I should be redirected to the URL "https://www.saucedemo.com/"
    And the page title should be "Swag Labs"
    And the page URL path should be "/"
    And the logo should be displayed
    And I should see the login form
    And login form should contain the following fields:
      | Username field |
      | Password field |
      | Login button   |
    And login users credentials should be visible in the login form:
      | Username field          |
      | standard_user           |
      | locked_out_user         |
      | problem_user            |
      | performance_glitch_user |
      | error_user              |
      | visual_user             |
    And the shared password should be visible in the login form

  @Smoke
  Scenario: Successful login opens the product catalog
    When I log in as "standard_user" using the "valid" password
    Then I should be on the "products" page
    And the page title should be "Swag Labs"
    And the page URL path should be "/inventory.html"
    And I should see the page header as "Products"
    And the shopping cart badge should not be visible

  @Regression
  Scenario Outline: Login outcome for <username> using <passwordMode> password
    When I log in as "<username>" using the "<passwordMode>" password
    Then the login result should be "<result>"

    Examples:
      | username                | passwordMode | result     |
      | problem_user            | valid        | SUCCESS    |
      | performance_glitch_user | valid        | SUCCESS    |
      | error_user              | valid        | SUCCESS    |
      | visual_user             | valid        | SUCCESS    |

  @Negative @Regression
  Scenario: Locked user sees the correct rejection
    When I log in as "locked_out_user" using the "valid" password
    Then I should see the error message "Epic sadface: Sorry, this user has been locked out."
    And I close the error message

  @Negative @Regression
  Scenario Outline: Login field validation shows the expected message
    When I log in as "<username>" using the "<passwordMode>" password
    Then I should see the error message "<error>"

    Examples:
      | username      | passwordMode | error                              |
      |               | valid        | Epic sadface: Username is required |
      | standard_user | empty        | Epic sadface: Password is required |

  @Negative @Regression
  Scenario Outline: Invalid credentials show the expected message
    When I log in as "<username>" using the "<passwordMode>" password
    Then I should see the error message "Epic sadface: Username and password do not match any user in this service"

    Examples:
      | username     | passwordMode |
      | invalid_user | valid        |
      | standard_user | invalid      |

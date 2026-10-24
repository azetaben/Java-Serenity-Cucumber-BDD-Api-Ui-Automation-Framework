@UI @Login @PasswordManagerPrompt
Feature: Native Google Password Manager after login
  This opt-in scenario requires visible local Chrome on an interactive English Windows desktop.

  Scenario: Successful login displays a native password prompt
    Given native Password Manager observation is enabled
    And I open the browser on the SauceDemo page
    When I log in as "standard_user" using the "valid" password
    Then I should be on the "products" page
    And I wait up to 5 seconds for Google Password Manager to appear

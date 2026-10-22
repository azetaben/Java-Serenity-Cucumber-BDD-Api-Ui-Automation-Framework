@UI @Security
Feature: Login security

  Background:
    Given I open the browser on the SauceDemo page

  @SQLInjection
  Scenario Outline: SQL injection payloads cannot bypass authentication
    When I log in as "<username>" using the "<passwordMode>" password
    Then the login result should be "FAILURE"
    And the session cookie should be absent

    Examples:
      | username               | passwordMode |
      | ' OR '1'='1            | invalid      |
      | admin'--               | invalid      |
      | standard_user'--       | valid        |
      | '; DROP TABLE users;-- | invalid      |

  @XSS
  Scenario Outline: Script payloads in credentials are neither executed nor rendered
    When I login with "<payload>" and "<payload>"
    Then the login result should be "FAILURE"
    And no injected script should have run or been rendered

    Examples:
      | payload                            |
      | <img src=x onerror=window.__xss=1> |
      | <script>window.__xss=1</script>    |
      | javascript:window.__xss=1          |

  @PasswordMasking
  Scenario: The password field masks what is typed
    When I login with "standard_user" and "not-the-password"
    Then the password field should be masked

  @AccessControl
  Scenario Outline: Protected pages are not reachable without a session
    When I open the "<page>" page without logging in
    Then I should be redirected to "login" page
    And the login error should contain "when you are logged in"
    And the session cookie should be absent

    Examples:
      | page                 |
      | inventory            |
      | cart                 |
      | checkout information |
      | checkout overview    |
      | checkout complete    |

  @UserEnumeration
  Scenario: Failed logins do not reveal whether an account exists
    When I login with "no_such_user_12345" and "wrong-password"
    Then I remember the login error
    When I log in as "standard_user" using the "invalid" password
    Then the login error should be identical to the remembered error

  @LockedAccount
  Scenario: A locked-out account cannot authenticate
    When I attempt to log in as "locked_out_user" with the shared password
    Then the login error should contain "locked out"
    And I should be redirected to "login" page
    When I open the "inventory" page without logging in
    Then I should be redirected to "login" page
    And the login error should contain "when you are logged in"

  @SessionTermination
  Scenario: Logging out ends the session and protected pages become inaccessible
    When I login as accepted user
    Then the session cookie should be present
    When I tap burger menu and select "Logout" from the burger menu list
    Then I should be redirected to "login" page
    And the session cookie should be absent
    When I navigate back in the browser
    Then I should be redirected to "login" page
    And the login error should contain "when you are logged in"

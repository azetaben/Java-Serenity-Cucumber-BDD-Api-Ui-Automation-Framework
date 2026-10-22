@ui @auth @login @regression @BurgerMenu
Feature: burger menu links
  As a user
  I want the burger menu links to work
  So that I can move through the application

  Background:
    Given I prepare the "inventory" page
    When I open the burger menu
    Then the burger menu list should contain:
      | All Items       |
      | About           |
      | Logout          |
      | Reset App State |
    And the burger menu close button is displayed
    And I close the burger menu

  @TC_LLF_020
  Scenario: All Items link is clickable
    When I select "All Items" from the burger menu list
    Then I should be redirected to "inventory" page

  @TC_LLF_016
  Scenario: Logout link is clickable
    When I open the burger menu
    When I select "logout" from the burger menu list
    Then I should be redirected to "login" page

  @TC_LLF_017
  Scenario: Reset App State link is clickable
    And I tap burger menu and select "Reset App State" from the burger menu list
    Then I should be redirected to "inventory" page
    Then the shopping cart badge should not be visible

  @TC_LLF_015
  Scenario: About link is clickable
    When I open the burger menu
    And I tap burger menu and select "About" from the burger menu list

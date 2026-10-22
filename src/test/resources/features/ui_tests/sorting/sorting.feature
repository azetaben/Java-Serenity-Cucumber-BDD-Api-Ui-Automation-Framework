@UI
Feature: Inventory and sorting options

  @ProductionSafe @Regression @Sorting
  Scenario Outline: Standard account sorts the complete catalog
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "STANDARD_USER"
    Then I am on the inventory page
    When I sort the inventory by "<sort>"
    Then the inventory should be ordered by "<field>" "<direction>"
    Examples:
      | sort              | field | direction  |
      | name a to z       | name  | ascending  |
      | name z to a       | name  | descending |
      | price low to high | price | ascending  |
      | price high to low | price | descending |


  @AccountBehaviour @Sorting @bug
  Scenario Outline: Sorting for problem_user
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "PROBLEM_USER"
    Then I am on the inventory page
    When I sort the inventory by "<sort>"
    Then the inventory should be ordered by "<field>" "<direction>"
    Examples:
      | sort              | field | direction  |
      | name a to z       | name  | ascending  |
      | name z to a       | name  | descending |
      | price low to high | price | ascending  |
      | price high to low | price | descending |

  @AccountBehaviour @Sorting
  Scenario Outline: Sorting for performance_glitch_user
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "PERFORMANCE_GLITCH_USER"
    Then I am on the inventory page
    When I sort the inventory by "<sort>"
    Then the inventory should be ordered by "<field>" "<direction>"
    Examples:
      | sort              | field | direction  |
      | name a to z       | name  | ascending  |
      | name z to a       | name  | descending |
      | price low to high | price | ascending  |
      | price high to low | price | descending |

  @AccountBehaviour @Sorting @bug
  Scenario Outline: Sorting for error_user
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "ERROR_USER"
    Then I am on the inventory page
    When I sort the inventory by "<sort>"
    Then the inventory should be ordered by "<field>" "<direction>"
    Examples:
      | sort              | field | direction  |
      | name a to z       | name  | ascending  |
      | name z to a       | name  | descending |
      | price low to high | price | ascending  |
      | price high to low | price | descending |

  @AccountBehaviour @Sorting @bug
  Scenario Outline: Sorting for visual_user
    Given I open the browser on the SauceDemo page
    When I attempt to log in as "VISUAL_USER"
    Then I am on the inventory page
    When I sort the inventory by "<sort>"
    Then the inventory should be ordered by "<field>" "<direction>"
    Examples:
      | sort              | field | direction  |
      | name a to z       | name  | ascending  |
      | name z to a       | name  | descending |
      | price low to high | price | ascending  |
      | price high to low | price | descending |

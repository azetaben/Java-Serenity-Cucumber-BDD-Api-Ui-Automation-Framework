@manual @UI
Feature: Generate PDF order
  # The public SauceDemo application does not expose an order PDF feature.
  # As a customer, I want to be able to download the order PDF from the checkout
  # complete page so that I can have a record of my purchase.

  @PdfDownload
  Scenario: 01 - Should be able to download PDF from checkout complete page
    Given I prepare the "checkout complete" page
    When I generate and download the order PDF into downloadfiles
    Then I select the "logout" menu option
    And I should be redirected to "login" page

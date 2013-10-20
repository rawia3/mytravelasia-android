Feature: Ads

  Scenario: As a user I can click on ads
    Given that I am at the StartupActivity
    And an ad is at the bottom of the screen
    When I press the ad
    Then I should be taken to the browser

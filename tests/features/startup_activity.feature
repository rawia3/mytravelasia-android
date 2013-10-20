Feature: Startup Enter Feature

  Scenario: As a user I can enter the app
    Given that I am at the StartupActivity
    When I press "Enter"
    Then I should see a list of places near me

Feature: Enter Main Activity

  Scenario: As a user I can enter the MainActivity
    Given that I am at the StartupActivity
    And I can see the "Enter" button
    When I press the "Enter" button
    Then I should be taken to the MainActivity

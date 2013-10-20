Feature: Near Me List

  Scenario: As a user I can view a list of places near me
    Given that I am viewing the list of places near me
    When I scroll to the bottom of the list
    Then the last item's distance to me should not be greater than 1100m

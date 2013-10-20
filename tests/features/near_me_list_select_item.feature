Feature: Select Item From Near Me List

  Scenario: As a user I can select an item from the list of places near me
    Given that I am viewing the list of places near me
    When I select the first item
    Then I should see a map
    And I should see a list of thumbnails at the left side

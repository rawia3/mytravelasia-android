Feature: Side Navigation

  Scenario: As a user I can click on the sandwich icon to view the side navigation
    Given that I am viewing the list of places near me
    Then I should see the sandwich icon at the top left of the screen
    When I click the sandwich icon
    Then I should see the side navigation
    Then I should see "Home" as the first item
    # There should be more items listed here, but it's hard to specify which can be seen for different device sizes
    When I scroll down the side navigation
    Then I should see "Tour and Travel Agencies" as the last item

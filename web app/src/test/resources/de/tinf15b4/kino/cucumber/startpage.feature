Feature: Start Page

  Scenario: Open the Start Page
    Given I am not logged in
    When I open the start page
    Then I should see a label containing smartCinema
    Then I should see a button labeled Anmelden
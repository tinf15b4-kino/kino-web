Feature: Start Page

  Scenario: Open the Start Page
    Given I am not logged in
    When I open the start page
    Then I should see a label containing smartCinema
    Then I should see a button labeled Anmelden

  Scenario: Modified Start Page for Authenticated User
    Given I am logged in as Martina Musterfrau
    When I open the start page
    Then I should see a label containing Martina Musterfrau
    Then I should see a button labeled Abmelden

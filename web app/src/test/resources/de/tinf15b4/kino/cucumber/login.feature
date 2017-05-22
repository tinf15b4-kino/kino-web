Feature: Login
  Scenario: Complete Login form using keyboard only
    Given the users
      | Name            | Password  |
      | Max Mustermann  | muster    |

    When I open the start page
    And I click the button labeled "Filme"
    # HACK I don't exactly know why this is necessary
    And I wait until a label containing "Anmelden" is visible
    And I click the button labeled "Anmelden"
    And I type "Max Mustermann" into "#loginUsernameField"
    And I press TAB
    And I type "muster"
    And I press RETURN

    # Poor man's success detector
    Then the current URL should be "#!movies"

  Scenario: Complete Login form using the mouse
    Given the users
      | Name            | Password  |
      | Max Mustermann  | muster    |

    When I open the start page
    And I click the button labeled "Filme"
    # HACK I don't exactly know why this is necessary
    And I wait until a label containing "Anmelden" is visible
    And I click the button labeled "Anmelden"
    And I type "Max Mustermann" into "#loginUsernameField"
    And I type "muster" into "#loginPasswordField"
    And I click on "#loginSubmitButton"

    # Poor man's success detector
    Then the current URL should be "#!movies"

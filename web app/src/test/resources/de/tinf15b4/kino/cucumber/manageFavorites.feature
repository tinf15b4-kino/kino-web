Feature: Manage favorites
	As an registered User
	I want to mark a cinema as a favorite, to find it faster and easier on the next visit.

  Scenario: add favorite on cinema page
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
      | 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
      | 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the link labeled "Schicke Scheinwand"
    # HACK: The page switching has a light delay, and the previous page already contains the same button.
    # therefore, we'll wait until we are definitely on the cinema detail page
    And I wait until an element containing "EpicStreet" appears
    And I click the button labeled "Favorit" below "#cinemaInfoBox_2"

    Then I should see a label containing "als Favorit markiert"
    And the database should have saved cinema 2 as favorite

  Scenario: add favorite on cinema list page
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the button labeled "Favorit" below "#cinemaRow_2"

    Then I should see a label containing "als Favorit markiert"
    And the database should have saved cinema 2 as favorite

  Scenario: List Favorites
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |
    And the favorite cinemas
      | User            | Cinema  |
      | Max Mustermann  | 2       |
      | Max Mustermann  | 1       |

    When I open the start page
    And I click the button labeled "Favoriten"

    Then I should see a label containing "Schicke Scheinwand"
    And I should see a label containing "Maier, Maier und Maier"

  Scenario: Delete favorite from favorite list
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |
    And the favorite cinemas
      | User            | Cinema  |
      | Max Mustermann  | 2       |
      | Max Mustermann  | 1       |

    When I open the start page
    And I click the button labeled "Favoriten"
    And I click the button labeled "Favorit" below "#cinemaRow_2"

    Then I should see a label containing "als Favorit entfernt"
    # TODO: Reimplement the undo feature
    # And I should see a label containing "Rückgängig machen"
    And the database should not have saved cinema 2 as favorite
    And the database should have saved cinema 1 as favorite

  # TODO: Reimplement the undo feature
  # Scenario: Delete from favorite list, then undo
  #  Given I am logged in as "Max Mustermann"
  #  And the cinemas
  #    | Id  | Name                    |
  #    | 1   | Maier, Maier und Maier  |
  #    | 2   | Schicke Scheinwand      |
  #  And the favorite cinemas
  #    | User            | Cinema  |
  #    | Max Mustermann  | 2       |
  #    | Max Mustermann  | 1       |

  #  When I open the start page
  #  And I click the button labeled "Favoriten"
  #  And I click the button labeled "Favorit" below "#cinemaRow_2"
  #  # And I click the button labeled "Rückgängig machen" below ".favorite-cinema-row-2"
  #  # HACK: UI lag causes us to click the wrong button, so we have to wait until the right one is there
  #  And I wait until every element containing "wurde aus den Favoriten entfern" disappears

  #  Then I should see a label containing "Entfernen" below "#cinemaRow_2"
  #  And the database should have saved cinema 2 as favorite

  Scenario: Remove favorite from cinema page
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
      | 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
      | 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |
    And the favorite cinemas
      | User            | Cinema  |
      | Max Mustermann  | 2       |
      | Max Mustermann  | 1       |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the link labeled "Schicke Scheinwand"
    # HACK: The page switching has a light delay, and the previous page already contains the same button.
    # therefore, we'll wait until we are definitely on the cinema detail page
    And I wait until an element containing "EpicStreet" appears
    And I click the button labeled "Favorit" below "#cinemaInfoBox_2"

    Then I should see a label containing "als Favorit entfernt"
    And the database should not have saved cinema 2 as favorite

  Scenario: Remove favorite from cinema list
    Given I am logged in as "Max Mustermann"
    And the cinemas
      | Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
      | 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
      | 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |
    And the favorite cinemas
      | User            | Cinema  |
      | Max Mustermann  | 2       |
      | Max Mustermann  | 1       |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the button labeled "Favorit" below "#cinemaRow_2"

    Then I should see a label containing "als Favorit entfernt"
    And the database should not have saved cinema 2 as favorite

  Scenario: Show favorites without login
    Given I am not logged in
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |

    When I open the start page
    And I click the button labeled "Favoriten"

    Then I should see a label containing "Sie müssen sich anmelden"

  Scenario: Add favorite on cinema page without login
    Given I am not logged in
    And the cinemas
      | Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
      | 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
      | 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the link labeled "Schicke Scheinwand"
    # HACK: The page switching has a light delay, and the previous page already contains the same button.
    # therefore, we'll wait until we are definitely on the cinema detail page
    And I wait until an element containing "EpicStreet" appears
    And I click the button labeled "Favorit" below "#cinemaInfoBox_2"

    # TODO: The Use-case specification requires us to redirect to the login page
    # instead, but that's not going to be implemented this year
    Then I should see a label containing "Melden Sie sich an, um diese Funktion nutzen zu können"

  Scenario: Add favorite on cinema list without login
    Given I am not logged in
    And the cinemas
      | Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
      | 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
      | 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |

    When I open the start page
    And I click the button labeled "Kinos"
    And I click the button labeled "Favorit" below "#cinemaRow_2"

    # TODO: The Use-case specification requires us to redirect to the login page
    # instead, but that's not going to be implemented this year
    Then I should see a label containing "Melden Sie sich an, um diese Funktion nutzen zu können"

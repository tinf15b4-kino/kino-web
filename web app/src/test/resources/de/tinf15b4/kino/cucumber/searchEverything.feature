Feature: Search everything
  As an User
  I want to search for anything (movies, cinemas, descriptions, ...) by typing in the searched value into a single field.

  Scenario: Search in movie titles
    Given the movies
      | Id  | Name            | Description |
      | 1   | Donald Duck     | Yo Mama     |
      | 2   | Daisy Duck      | Yo Mama     |
      | 3   | Weihnachtsmann  | Yo Mama     |

    When I open the start page
    And I search for "duck"

    Then I should see a label containing "Donald Duck"
    And I should see a label containing "Daisy Duck"
    And the link "Donald Duck" should redirect to "#!movie/1"

  Scenario: Search in cinema names
    Given the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |

    When I open the start page
    And I search for "schick"

    Then I should see a label containing "Scheinwand"
    And the link "Schicke Scheinwand" should redirect to "#!cinema/2"

  Scenario: Search in both cinemas and movies
    Given the movies
      | Id  | Name            | Description |
      | 1   | Donald Duck     | Yo Mama     |
      | 2   | Daisy Maier     | Yo Mama     |
      | 3   | Weihnachtsmann  | Yo Mama     |
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |

    When I open the start page
    And I search for "Maier"

    Then I should see a label containing "Daisy Maier"
    And I should see a label containing "Maier, Maier und Maier"

  Scenario: Fulltext search in movie descriptions
    Given the movies
      | Id  | Name            | Description                                       |
      | 1   | Donald Duck     | O Romeo, Romeo! Wherefore art thou Romeo?         |
      | 2   | Daisy Maier     | Deine Mama ist so fett, ihr Gürtel heißt Äquator  |
      | 3   | Lombardi        | Du bist ne Schlampe, ich bin normaler Mensch!     |

    When I open the start page
    And I search for "Schlampe"

    Then I should see a label containing "Lombardi"
    And I should not see a label containing "Daisy Maier"

  Scenario: Fulltext search in cinema addresses
    Given the cinemas
      | Id  | Name                    | Street          | City                  |
      | 1   | Maier, Maier und Maier  | Gina-Lisa-Str.  | Belgien               |
      | 2   | Schicke Scheinwand      | Stramme Straße  | Neustadt an der Lisa  |

    When I open the start page
    And I search for "Lisa"

    Then I should see a label containing "Schicke Scheinwand"
     And I should see a label containing "Maier"

  Scenario: No results
    Given the movies
      | Id  | Name            | Description |
      | 1   | Donald Duck     | Yo Mama     |
      | 2   | Daisy Maier     | Yo Mama     |
      | 3   | Weihnachtsmann  | Yo Mama     |
    And the cinemas
      | Id  | Name                    |
      | 1   | Maier, Maier und Maier  |
      | 2   | Schicke Scheinwand      |

    When I open the start page
    And I search for "Weihnachtsfrau"

    Then I should not see a link labeled "Weihnachtsfrau"

  Scenario: Search with enter key
    When I open the start page
    And I type "nemo" into "#cinemaSearchBox"
    And I press ENTER

    Then the current URL should be "#!search/nemo"

  Scenario: Search with mouse click
    When I open the start page
    And I type "nemo" into "#cinemaSearchBox"
    And I click on "#cinemaSearchBtn"

    Then the current URL should be "#!search/nemo"

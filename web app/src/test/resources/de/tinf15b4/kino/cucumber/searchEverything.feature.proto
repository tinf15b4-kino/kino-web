Feature: Search everything
	As an User
	I want to search for anything (movies, cinemas, descriptions, ...) by typing in the searched value into a single field.

  Scenario: Search in movie titles 
    Given The search-field is available 

    When I type a name of a movie into the search-field

    Then The movie should be listet in the search result
    And If you click onto the movie you should get to the detail-view of the movie

  Scenario: Search in cinema names
    Given The search-field is available 

    When I type a name of a cinema into the search-field

    Then The cinema should be listet in the search result
    And If you click onto the movie you should get to the detail-view of the cinema

  Scenario: Full text search in cinema/movie-descriptions
    Given The search-field is available 

    When I type any words that are contained in a desription into the search-field

    Then The cinema/movie with that description should be listet in the search result
    And If you click onto the cinema/movie you should get to the detail-view of the cinema/movie

  Scenario: No results
    Given The search-field is available 

    When I type a text that is not listet anywhere into the search-field

    Then The user should get an idication, that such text is not existing 

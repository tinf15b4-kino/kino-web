Feature: Manage favorites
	As an Registerd User
	I want to mark a cinema as a favorite, to find it faster and easier on the next visit.

  Scenario: add favorite
    Given I am logged in 
    And I have a list of cinemas 
    And At least one of them is no favorite yet

    When I click the mark as favorite button

    Then This cinema should be added as favorite
    And The mark as favorite button should swap to a remove favorite button

  Scenario: delete favorite 
  	Given I am logged in 
    And I have a list of cinemas 
    And At least one of them is a favorite

    When I click the remove as favorite button

    Then This cinema should be removed as favorite
    And The mark as favorite button should swap to a remove favorite button
    And The user should get asked, if he is sure to remove 
  	And If the user click <sure> the the cinema should be <databse>

  	Examples:
  		| sure |           databse           | 
  		| undo | added to the database again |
  		| yes  |  removed from the database  |
Feature: Cinema View 
	As an User
  I want to see all cinemas in a list and get information about them

Scenario: See the cinema list 
	Given the cinemas 
		| Id  | Name                    |
		| 1   | Maier, Maier und Maier  |
		| 2   | Schicke Scheinwand      |
		
	When I open the start page 
	And I click the button labeled "Kinos"
	
	Then I should see a label containing "Maier, Maier und Maier"
	And I should see a label containing "Schicke Scheinwand"
	And all elements matching "[id^=cinemaImage]" are left-aligned at 300x400,800x600,1024x768,1280x720
	And all elements matching "[id^=cinemaListLink]" are left-aligned at 300x400,800x600,1024x768,1280x720
	And all elements matching "[id^=cinemaFavoriteButton]" are left-aligned at 300x400,800x600,1024x768,1280x720
	And the link "Maier, Maier und Maier" should redirect to "#!cinema/1"

	
Scenario: See the cinema information 
	Given the cinemas 
		| Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
		| 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
		| 2   | Schicke Schummerwand    | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |
		
	When I open the start page 
	And I click the button labeled "Kinos"
	And I click the link labeled "Schicke Schummerwand"
	
	Then the current URL should be "#!cinema/2"
	And I should see a label containing "Schicke Schummerwand"
	And I should see a label containing "EpicStreet 23"
	And I should see a label containing "12345 Timbuktu"
	
	
Scenario: See the cinema rating 
	Given the cinemas 
		| Id  | Name                    | Street     | Hnr | Postcode | City        | Country     |
		| 1   | Maier, Maier und Maier  | NiceStreet | 17  | 99244    | Laurenstadt | Deutschland |
		| 2   | Schicke Scheinwand      | EpicStreet | 23  | 12345    | Timbuktu    | Somewhere   |
	And the users 
		| Id  | Name       |
		| 1   | Mustermann |
	And the rating of User "Mustermann" for Cinema "Schicke Scheinwand" with 4 stars and description "This is a nice Cinema"
	
	When I open the start page 
	And I click the button labeled "Kinos"
	And I click the link labeled "Schicke Scheinwand"
	
	Then I should see a label containing "Schicke Scheinwand"
	And I should see a label containing "Mustermann"
	And I should see a label containing "4"
	And I should see a label containing "This is a nice Cinema"
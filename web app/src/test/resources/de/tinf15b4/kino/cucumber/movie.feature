Feature: Movie View 
	As an User
  I want to see all movies in a list and get information about them

Scenario: See the movie list 
	Given the movies 
		| Id  | Name            | Description |
		| 1   | Donald Duck     | Yo Mama     |
		| 2   | Daisy Duck      | Yo Mama     |
		| 3   | Weihnachtsmann  | Yo Mama     |
		
	When I open the start page 
	And I click the button labeled Filme 
	
	Then I should see a label containing Donald Duck 
	And I should see a label containing Daisy Duck 
	And I should see a label containing Weihnachtsmann 
	And the link Donald Duck should redirect to #!movie/1
	
	
Scenario: See the movie information 
	Given the movies 
		| Id  | Name            | Description | LengthMinutes |
		| 1   | Donald Duck     | Yo Mama     | 37			  |
		| 2   | Daisy Duck      | Yo Mama     | 123			  |
		| 3   | Weihnachtsmann  | Yo Mama     | 456			  |
		
	When I open the start page 
	And I click the button labeled Filme 
	And I click the link labeled Donald Duck 
	
	Then I should see a label containing Donald Duck 
	And I should see a label containing 37 Minuten 
	And I should see a label containing Yo Mama 
	
	
Scenario: See the movie rating 
	Given the movies 
		| Id  | Name            | Description | LengthMinutes |
		| 1   | Donald Duck     | Yo Mama     | 37			  |
		| 2   | Daisy Duck      | Yo Mama     | 123			  |
		| 3   | Weihnachtsmann  | Yo Mama     | 456			  |
	And the users 
		| Id  | Name       |
		| 1   | Mustermann |
	And the rating of User Mustermann for Movie Donald Duck with 4 stars and description This is a nice Movie 
	
	When I open the start page 
	And I click the button labeled Filme 
	And I click the link labeled Donald Duck 
	
	Then I should see a label containing Donald Duck 
	And I should see a label containing Mustermann 
	And I should see a label containing 4 
	And I should see a label containing This is a nice Movie
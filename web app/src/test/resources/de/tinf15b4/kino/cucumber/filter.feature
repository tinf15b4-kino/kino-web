Feature: Filter movie view 
	As an User
  I want to filter the movies in the movie list view based on criteria like price range, playtime, genre and fsk rating

Scenario: See movie list with active fsk filter 
	Given the movies 
		| Id  | Name            | Description | AgeControl 	|
		| 1   | Donald Duck     | Yo Mama     |	USK0		|
		| 2   | Daisy Duck      | Yo Mama     |	USK0		|
		| 3   | Weihnachtsmann  | Yo Mama     |	USK6		|
	And the cinemas 
		| Id  | Name                    |
		| 1   | Maier, Maier und Maier  |
	And movie 1 is played in cinema 1 for 100 cents 
	And movie 2 is played in cinema 1 for 100 cents 
	And movie 3 is played in cinema 1 for 100 cents 
	
	When I open the start page 
	And I click the button labeled "Filme" 
	And I toggle checkbox filter "FSK 0" 
	
	Then I should see a label containing "Donald Duck" 
	And I should see a label containing "Daisy Duck" 
	And I should not see a label containing "Weihnachtsmann" 
	
	When I toggle checkbox filter "FSK 6" 
	Then I should see a label containing "Donald Duck" 
	And I should see a label containing "Daisy Duck" 
	And I should see a label containing "Weihnachtsmann" 
	
Scenario: See the movie list with active genre filter 
	Given the movies 
		| Id  | Name            | Description | Genre		|
		| 1   | Donald Duck     | Yo Mama     | Action		|
		| 2   | Daisy Duck      | Yo Mama     | Crime	  	|
		| 3   | Weihnachtsmann  | Yo Mama     | Drama	  	|
	And the cinemas 
		| Id  | Name                    |
		| 1   | Maier, Maier und Maier  |
	And movie 1 is played in cinema 1 for 100 cents 
	And movie 2 is played in cinema 1 for 100 cents 
	And movie 3 is played in cinema 1 for 100 cents 
	
	When I open the start page 
	And I click the button labeled "Filme" 
	And I toggle checkbox filter "Action" 
	
	Then I should see a label containing "Donald Duck" 
	And I should not see a label containing "Daisy Duck" 
	And I should not see a label containing "Weihnachtsmann" 
	
	When I toggle checkbox filter "Drama" 
	
	Then I should see a label containing "Donald Duck" 
	And I should not see a label containing "Daisy Duck" 
	And I should not see a label containing "Weihnachtsmann" 
	
	
Scenario: See the movie list with active price filter 
	Given the movies 
		| Id  | Name            | Description |
		| 1   | Donald Duck     | Yo Mama     |
		| 2   | Daisy Duck      | Yo Mama     |
		| 3   | Weihnachtsmann  | Yo Mama     |
	And the cinemas 
		| Id  | Name                    |
		| 1   | Maier, Maier und Maier  |
	And movie 1 is played in cinema 1 for 100 cents 
	And movie 2 is played in cinema 1 for 200 cents 
	And movie 3 is played in cinema 1 for 300 cents 
	
	When I open the start page 
	And I click the button labeled "Filme" 
	And I enter "0,99" in the lower-price filter 
	And I enter "1,99" in the upper-price filter 
	
	Then I should see a label containing "Donald Duck" 
	And I should not see a label containing "Daisy Duck" 
	And I should not see a label containing "Weihnachtsmann" 
	
	And I enter "0,99" in the lower-price filter 
	And I enter "2,00" in the upper-price filter 
	
	Then I should see a label containing "Donald Duck" 
	And I should see a label containing "Daisy Duck" 
	And I should not see a label containing "Weihnachtsmann"
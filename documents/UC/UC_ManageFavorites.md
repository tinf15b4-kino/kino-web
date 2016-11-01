#Use-Case-Specification: Manage favorites
##Table of Contents
tbd    

### 1. Manage Favorites
#### 1.1 Brief Description
The manage favorites function allows the users of smartCinema to mark their favorite cinemas.
Marked cinemas will be shwon in a special-tab so the user can easily look up, which movies are shown in this cinema without seraching for them.


### 2.Flow of Events
####2.1 Add new favorite
![Add new favorite flow][Add_flow]

####2.2 Remove a favorite
![Remove a favorite flow][Remove_flow]

#### 2.3 Alternative Flows
(n/a)


### 3.Special Requirements
(n/a)


### 4. Preconditions
#### 4.1 Register 
To use the manage favorites function you have to create an account for the smartCinema web application.
Since you are registerd and logged-in in your account, it is possible to mark cinemas as a favorite and get the possiblity, to view and manage all of them in a special tab.


### 5. Postconditions
#### 5.1 Add new favorite
If a cinema got marked as a favorite, this cinema should be shown in the favorite cinemas-tab and the cinema should get an indication, that it is a favorite. Furthermore the option to mark this cinema as a favorite will be swaped to an option which allows the user to remove the favorite mark.

#### 5.2 remove a favorite
If the favorite mark of a cinema gets removed, this cinema shouldn't be shown in the favorite cinemas-tab anymore and the indication should be vanish. Furthermore the option to remove the favorite mark will be swapped to an option which allows the user to mark that ciname as a favorite (start value).  


### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[Add_flow]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/UC/manage%20favorites/activity%20diagram%20-%20add%20new%20favorite%20cinema.svg
[Remove_flow]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/UC/manage%20favorites/activity%20diagram%20-%20remove%20favorite%20from%20favorite%20list.svg

 

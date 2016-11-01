#Use-Case-Specification: Manage favorites
##Table of Contents
tbd    

### 1. Manage Favorites
#### 1.1 Brief Description
The manage favorites function allows the users of smartCinema to mark their favorite cinemas.
This cinemas will be shwon in a special-tab so the user can easily look up, which movies are shown in this cinema without seraching for them.


### 2.Flow of Events
####2.1 Basic Flow
![flow for manage favorites][flow]

#### 2.2 Alternative Flows
(n/a)


### 3.Special Requirements
(n/a)


### 4. Preconditions
#### 4.1 Register 
To use the manage favorites function you have to create an account with the smartCinema web application.
Since you are registerd and logged-in in your account, it is possible to mark cinemas as a favorite and get the possiblity, to view and manage all of them in a special tab.


### 5. Postconditions
#### 5.1 Add new favorite
If a cinema got marked as a favorite, this cinema should be shown in the favorite cinemas-tab and the cinema shpuld get an indication, that it is a favorite.

#### 5.2 remove a favorite
If the favorite mark gets removed, this cinema shouldn't be shown in the favorite cinemas-tab anymore and the indication should be vanish.


### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[flow]: [https://github.com/tinf15b4-kino/kino-web/blob/master/documents/UC/UC_SearchEverything.png

 

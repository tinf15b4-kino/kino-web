#Use-Case-Specification: Manage favorites  

### 1. Manage Favorites
#### 1.1 Brief Description
The "manage favorites" function allows the users of SmartCinema to mark their favorite cinemas.
These cinemas will be shown in a special tab so the user can easily look up which movies are shown in this cinema without searching for them.

This is a CRUD use case, albeit with no useful "update" action.


### 2.Flow of Events

#### 2.1/1 Basic Flow: Add new favorite

Activity Diagram:

![Add new favorite flow][Add_flow]

UI Mockup / before:

![Add new favorite mockup][Add_mockup]

UI Mockup / after:

![Favorite Cinema][Remove_mockup]

Feature-File:

[Link to the Feature-File](https://github.com/tinf15b4-kino/kino-web/blob/TESB416-89/web%20app/src/test/resources/de/tinf15b4/kino/cucumber/manageFavorites.feature.proto)

#### 2.1/2 Basic Flow: Remove a favorite (from favorite list)

Activity Diagram: 

![Remove a favorite flow][Remove_flow]

UI Mockup / before:

![Remove a favorite mockup][Remove_List_mockup]

UI Mockup / after:

![Favorite cinema removed mockup][Removed_from_list_mockup]

Feature-File:

[Link to the Feature-File](https://github.com/tinf15b4-kino/kino-web/blob/TESB416-89/web%20app/src/test/resources/de/tinf15b4/kino/cucumber/manageFavorites.feature.proto)

#### 2.1/3 Basic Flow: Show favorite cinemas

UI Mockups:

![Some favorite cinemas][Remove_List_mockup]

![No favorite cinemas][Favorite_List_Empty_mockup]

Feature-File:

[Link to the Feature-File](https://github.com/tinf15b4-kino/kino-web/blob/TESB416-89/web%20app/src/test/resources/de/tinf15b4/kino/cucumber/manageFavorites.feature.proto)

#### 2.2 Alternative Flows
(n/a)


### 3.Special Requirements
(n/a)


### 4. Preconditions

#### 4.1 Register
To use the "manage favorites" function, you have to create an account with the SmartCinema web application.

The "add new favorite" button is always visible, but will prompt the user to log in if necessary. The list of favorites and the "remove" button is only accessible to users which are currently logged in. 

### 5. Postconditions

#### 5.1 Add new favorite
If a cinema got marked as a favorite, this cinema should be shown in the "favorite cinemas"
tab. Cinemas which are marked as a favorite will be indicated as such.

Furthermore, the option to mark this cinema as a favorite will be exchanged for an option which allows the user to remove the favorite mark.

#### 5.2 remove a favorite
If the favorite mark gets removed, this cinema shouldn't be shown in the "favorite cinemas" tab anymore and the special indication should have vanished.

Furthermore, the option to remove the favorite mark will be exchanged for an option which allows the user to mark that cinema as a favorite (the initial value). 


### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[Add_flow]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/activity%20diagram%20-%20add%20new%20favorite%20cinema.svg
[Remove_flow]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/activity%20diagram%20-%20remove%20favorite%20from%20favorite%20list.svg
[Add_mockup]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/mark%20as%20favorite%20button%20on%20cinema%20page.svg
[Remove_mockup]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/unmark%20favorite%20on%20cinema%20page.svg
[Remove_List_mockup]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/unmark%20favorite%20on%20favorite%20list.svg
[Removed_from_list_mockup]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/favorite%20unmarked%20on%20favorite%20list.svg
[Favorite_List_Empty_mockup]: https://rawgit.com/tinf15b4-kino/kino-web/master/documents/UC/manage%20favorites/no%20favorite%20cinemas.svg

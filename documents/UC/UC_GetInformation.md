#Use-Case Specification: Get information   

### 1. Get information

#### 1.1 Brief Description

The get information function will allow every user to find informations about cinemas and movies.

A Movie have the following informations:
  - Ratings
  - Description 
  - Playtimes
  
A Cinema have the following informations:
  - Ratings
  - Adress
  - Playlist
  

### 2.Flow of Events

####2.1 Basic Flow

Activity Diagram: 

![flow for get information][flow]


Feature-File:

[Link to the Feature-File Movie][feature_movie]

[Link to the Feature-File Cinema][feature_cinema]


Screenshots:

List of Movies:

![Picture - List of Movies][screenshot_movielist]


Selected Movie:

![Picture - selected Movie][screenshot_movie]


List of Cinemas:

![Picture - List of Cinemas][screenshot_cinemalist]


Selected Movie:

![Picture - selected Cinema][screenshot_cinema]


#### 2.2 Alternative Flows

(n/a)

### 3.Special Requirements

#### 3.1 Performance

According to our [SRS][SRS] the user should get the information that he is looking for within five clicks or less.


### 4. Preconditions

No preconditions.
The User can always find all information he is looking for.
 
### 5. Postconditions

No postconditions.
After the User found the Information (or not) nothing have to be changed or saved.

### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[flow]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/get%20information/flow_GetInformation.png
[Screenshot_movielist]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/filter%20information/screenshot_FilterInformation.png
[Screenshot_movie]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/get%20information/screenshot_movie_GetInformation.png
[Screenshot_cinemalist]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/get%20information/screenshot_cinemalist_GetInformation.png
[Screenshot_cinema]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/get%20information/screenshot_cinema_GetInformation.png
[feature_cinema]: https://github.com/tinf15b4-kino/kino-web/blob/master/web%20app/src/test/resources/de/tinf15b4/kino/cucumber/cinema.feature
[feature_movie]: https://github.com/tinf15b4-kino/kino-web/blob/master/web%20app/src/test/resources/de/tinf15b4/kino/cucumber/movie.feature



 

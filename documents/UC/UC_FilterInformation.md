#Use-Case-Specification: Filter Information
##Table of Contents
tbd    

### 1. Filter Information 
#### 1.1 Brief Description
The "filter information" function allows the users of SmartCinema to filter the movies.

There will be filters for: 

- Genre
- FSK
- Playtime
- Date
- 3D
- Rating
- Price

### 2.Flow of Events

#### 2.1 Basic Flow

Activity Diagram:
![Get filtered information][Flow]

Feature-File:
[Link to the Feature-File][Feature File]


#### 2.2 Alternative Flows
(n/a)


### 3.Special Requirements
(n/a)


### 4. Preconditions

No preconditions.
The filter can be used without being logged in and the system can be in every state.

### 5. Postconditions

#### 5.1 Get filterd information
After the user set some filters, he should see the informations (movies), which matches with the filter.

### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[Flow]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UC/filter%20information/flow_FilterInformation.png
[Mockup]: tbd
[Feature File]: tbd

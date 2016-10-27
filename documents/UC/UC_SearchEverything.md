#Use-Case-Specification: Search everything
##Table of Contents
tbd    

### 1. Use-Case Name
#### 1.1 Brief Description
The search-everything function will search the whole website without restrictions to categories likes films, cinemas etc.
The user can enter any text and everything that matsches the given input will be displayed in the view.

### 2.Flow of Events
####2.1 Basic Flow
![flow for search everything][flow]

#### 2.2 Alternative Flows
(n/a)

### 3.Special Requirements
#### 3.1 Performance
According to our [SRS][SRS] the user should receive the result within one sceond after he submits his search text.

#### 3.2 Search result
The result of the search will be split into three parts:
- movies: all movies whose title contain the search query.
- cinemas: all cinemas whose name contain the search query.
- descriptions: all cinemas or movies whose descipription contains the search query.

### 4. Preconditions
No preconditions. The search function can be used without being logged and the system can be in every state.
 
### 5. Postconditions
#### 5.1 View
After submitting the search pattern, the result view should only show entries that contain the given search text.

### 6. Extension Points
(n/a)

<!-- Link definitions -->
[SRS]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/SRS.md
[flow]: [https://github.com/tinf15b4-kino/kino-web/blob/master/documents/UC/UC_SearchEverything.png

 

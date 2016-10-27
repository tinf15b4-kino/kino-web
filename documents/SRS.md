# SmartCinema - Software Requirements Specification

## 1. Introduction
### 1.1 Purpose
This SRS describes all specifications for "SmartCinema". The aim of our application is to gather all information about all movie theaters including movies, prices, schedules, locations in one easily accessible place. Our product consists of an Android app and a web application. 

As non-functional requirements, we demand all information to be up-to-date (and synchronized between Android app and web application). 
Both interfaces should load quickly (less than 1 second until the page is fully loaded on a residential DSL or UMTS mobile connection).

To ensure almost constant availability, we require 99% uptime and maintenance work must not impact the running production system.

### 1.2 Scope
This software specification applies to the whole "SmartCinema" application. Apart from the web application and the Android app, we will have the following components:
- **Database** where all information about our users, and information retrieved from movie theaters is being stored.
- **Data retrieval script** which fills (and updates) the database.
- **MVC layer** which serves the web page(s) and provides an API for the Android app.

### 1.3 Definitions, Acronyms and Abbreviations
In this section definitions and explanations of acronyms and abbreviations are listed to help the reader to understand these.

- **tbd** To be determined
- **Android** This is a mobile operating system developed by Google for primarily use on smartphones and tablets
- **MVC** Model View Controller, a software design pattern for implementing user interfaces on computers
- **UC** Use Case
- **UCD** Use Case Diagram
- **OUCD** Overall Use Case Diagram


### 1.4 References
|			Title									|	Date		|
|---------------------------------------------------|---------------|
| [SmartCinema Website](http://kino.tinf15b4.de) | 10/20/2016 |
| [SmartCinema Blog](http://smartcinemaproject.wordpress.com) | 10/20/2016 |
| [SmartCinema Git](https://github.com/tinf15b4-kino/kino-web) | 10/20/2016 |


### 1.5 Overview
The following chapters are about our vision and perspective, the software requirements, the demands we have, licensing and
the technical realisation of this project.

## 2. Overall Description
The aim of our application is to gather all information about all movie theaters in one easily accessible place.
Where can I watch my desired movie? When do I have to get there? How did other people think about it?
All those questions will be answered by our product. Get all those information on a website or check it out in our mobile app for Android.

![Overall use case diagram][oucd]

## 3. Specific Requirements
### 3.1 Functionality
#### 3.1.1 Account management
Creating an account is only required for making contributions to the project, consumption of information does not require an account. 
Users can utilize their Google account for authentication in our app, or alternatively register and log in with a classic email/password combination. The Android app attempts to automatically use the Google account on device as soon as the user tries to perform an operation which requires authentication. 
Having an account enables the user to save favorite movies and cinemas and use our rating system. The user is able to log out, and the account can be edited and deleted at any time. 

#### 3.1.2 User interface
Inside the application (both web and android app), users can see a list of all movies that are currently showing. If they select a movie, they get information about where and when the movie will be shown in the next week. Additionally, information about prices will be visible. There will also be a rating system for movies and cinemas, as well as a way to filter the information and mark movies/cinemas as favorites.

#### 3.1.3 Rating system
The rating system requires the user to log in. Users can write reviews and rate cinemas and movies with zero to five stars. All other users can see those ratings and reviews to decide whether they want to see this movie in the given cinema. Users can comment on other ratings.

#### 3.1.4 Favorites
To use favorites, users have to be logged in. Movies and cinemas marked as favorites will be shown preferably and can be filtered in the view. 

#### 3.1.5 Filter
There will be certain ways to filter the list of movies and cinemas. Available filters are: 
- name
- location/distance
- favorites
- genre
- schedule
- prices

#### 3.1.6 Overall search
The Overall search function will search the whole website without restrictions to categories likes films, cinemas etc.

#### 3.1.7 Sorting
Information can be sorted by following criteria:
- name
- prices
- distance
- schedule (date and time)

#### 3.1.8 Reminders
To use reminders, users have to be logged in. Users can set a reminder for movies. At the beginning of a new cinema week (traditionally thursday) users will receive an email or push notification that links to our user interface with preset filters (assuming the movie is actually shown that week).

#### 3.1.9 Locations
By retrieving the users current location we can filter the cinemas based on their distance. In case the user does not permit our application to retrieve his location, there will be a way to enter his current position manually. There will also be a way to enter the maximum distance a user is willing to drive to the cinema. Location and maximum distance will be saved in the users account if available. 

#### 3.1.10 Database
We will save user accounts and all information associated with them in our database. All information about current movies, cinemas, schedules and prices will also be stored there.

#### 3.1.11 Receive data
Using a script we will update the information about movies, cinemas, schedule and prices daily. Those information will be stored in the database.

#### 3.1.12 Server (MVC layer)
The server will dynamically generate the web interface as well as provide an API for the Android app. 

### 3.2 Usability
#### 3.2.1 Trainings
Both web app and Android app need to carry intuitive, learnable interfaces. There will not be any manuals or training material. Users are assumed to know how to open a website in a modern browser or how to use their smartphone.

#### 3.2.2 Common tasks
Users should be able to find the movies they are looking for within a maximum of 3 clicks.

### 3.3 Reliability
#### 3.3.1 Up-time
Our server infrastructure should ensure a 99% uptime. 

#### 3.3.2 Mean Time Between Failures
Between two failures we expect to be at least one week.

#### 3.3.3 Mean Time To Repair
After a failure, the server and database should be up again in a maximum of four hours.

#### 3.3.4 Accuracy
Since our application monitors dates and appointments, 100% accuracy is pretty much a hard requirement.

#### 3.3.5 Bugs
There should not be any critical bugs in our live system. Those bugs are supposed to be caught by our test systems. 
We expect to have 5-10 minor and significant bugs per thousand lines of code.
Critical bugs make our systems unusable or can possibly corrupt our data. Significant bugs affect single functions which might be unavailable as a result. 

### 3.4 Performance
#### 3.4.1 Response times
Loading the user interface should be completed in less than one second, therefore, retrieving information from the database needs to be at least as fast, most likely faster to allow some time for other computations and rendering. Filtering and showing information about a specific movie/cinema should also be completed in less than a second. 

#### 3.4.2 Capacity
We expect to be able to handle 1000 concurrent users on the envisioned infrastructure at launch. The software architecture must be designed with scalability in mind to accomodate more users in the future.

#### 3.4.3 Degradation modes
If we can not fetch any new data from cinemas, we can use existing data for up to one week without significantly compromising on our accuracy goals. After this week has passed, users can still use all functionality of our system, except retrieving information about current prices and schedule.

If our servers are down, the application will not be available until the servers are back up again. 

### 3.5 Supportability
#### 3.5.1 IDE and coding conventions
All developers can use the IDE they perfer to edit the source code. Developers have to ensure they are using the same source code formatting and [codestyle][code style] as the others. The IDEs will probably be Eclipse and IntelliJ.

#### 3.5.2 Maintenance utilities
Maintenance work on our database will be done using phpMyAdmin. For other maintenance work on the server, whatever SSH client the developer prefers can be used.

### 3.6 Design Constraints
#### 3.6.1 Language support
We will use the following languages, which will also be well supported in the future:

- Java EE 8
- Android 4.1+
- Internet Standards HTML, CSS and JavaScript

#### 3.6.2 Process requirements
For organizing the development process we will use Scrum and JIRA. Version control is being done using [git][git] on [github][GitHub].

#### 3.6.3 architectural and design constraints
The architecture of our application will be based on the Spring MVC framework. Our database will be based on MySQL.

#### 3.6.4 Purchased components
The only purchased component is the server we are using (and strictly speaking it's not purchased but rather rented).

#### 3.6.5 Class libraries
tbd

### 3.7 Online User Documentation and Help System Requirements
Our application will not require any help system or user documentation as it will be straightforward and easy to use.

### 3.8 Purchased Components
see 3.6.4

### 3.9 Interfaces
#### 3.9.1 User Interfaces
tbd

#### 3.9.2 Hardware Interfaces
(n/a)

#### 3.9.3 Software Interfaces
If available, our system will use an API provided by cinemas to retrieve information about movies, schedule and prices.

Automatic login with a Google account will depend on the Google API.

#### 3.9.4 Communications Interfaces
(n/a)

### 3.10 Licensing Requirement
(n/a)

### 3.11 Legal, Copyright and other Notices
(n/a)

### 3.12 Applicable Standards
(n/a)

## 4. Supporting Information
### 4.1 Table of contents
tbd

### 4.2 Appendices
You can find any internal linked sources in the chapter References (go to the top of this document). If you would like to know what the current status of this project is please visit the [SmartCinema Blog][blog].


<!-- Link definitions: -->
[codestyle]: https://google.github.io/styleguide/javaguide.html
[blog]: https://smartcinemaproject.wordpress.com/
[git]: https://git-scm.com/
[github]: https://github.com/
[oucd]: https://github.com/tinf15b4-kino/kino-web/blob/master/documents/UC/OUCD.png

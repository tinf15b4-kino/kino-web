# SmartCinema - Software Requirements Specification

## 1. Introduction
### 1.1 Purpose
This SRS describes all specifications for "SmartCinema". The aim of our application is to gather all information about all movie theaters including movies, prices, schedules, locations in one easily accessible place. Our product consists of an Android app and a webapplication. 

Our non functional requirements are that all information has to be up-to-date (synchronized between Android app and webapplication) and both interfaces should load quickly (<1 seconds). Our apps are supposed to be stable. Our aim is to have 99% up-time so all maintenance systems should not impact the running system.

### 1.2 Scope
This software specification applies to the whole "SmartCinema" application. Apart from the webapplication and the Android app we will have the following components:
- **Database** where we will store all information about our users and information retrieved from movie theaters.
- **Data retrieval script** which will fill and update the database.
- **MVC layer** which generates the web page(s) and provides information for the Android app.

### 1.3 Definitions, Acronyms and Abbreviations
In this section definitions and explanations of acronyms and abbreviations are listed to help the reader to understand these.

- **tbd** To be done
- **Android** This is a mobile operating system developed by Google for primarily use on smartphones and tablets.
- **MVC** Model View Controller
- **UC** Use Case
- **UCD** Use Case Diagram
- **OUCD** Overall Use Case Diagram


### 1.4 References
|			Title									|	Date		|
|---------------------------------------------------|---------------|
| [SmartCinema Website](http://kino.tinf15b4.de) | 20.10.2016 |
| [SmartCinema Blog](http://smartcinemaproject.wordpress.com) | 20.10.2016 |
| [SmartCinema Git](https://github.com/tinf15b4-kino/kino-web) | 20.10.2016 |
| [Overall Use Case Diagram (OUCD)](https://github.com/tinf15b4-kino/kino-web/blob/fa2cce4e9da5553f88eb65997235257ff6776503/documents/uml.png)| 20.10.2016 |


### 1.5 Overview
The following chapters are about our vision and perspective, the software requirements, the demands we have, licensing and
the technical realisation of this project.

## 2. Overall Description
The aim of our application is to gather all information about all movie theaters in one easily accessible place.
Where can I watch my desired movie? When do I have to get there? How did other people think about it?
All those questions will be answered by our product. Get all those information on a website or check it out in our mobile app for Android.

## 3. Specific Requirements
### 3.1 Functionality
#### 3.1.1 Accountmanagement
Users can register or use their google account for our application. Creating an account is optional. User can log in to their account to save favourit movies and cinemas and use our rating system. When they are finished they can log out. This account can be edited and deleted at any time. 

#### 3.1.2 User interface
In our application (web and android app) users can see a list of all available movies. If they select a movie they get information about where and when the movie will be shown in the next week. Additioanlly information about prices will be visible. There will also be a rating system for movies and cinemas, as well as a way to filter the information and mark movies/cinemas as favourites.

#### 3.1.3 Rating system
To use the rating system users have to be logged in. They can write reviews and rate cinemas and movies with zero to five stars. All other users can see those ratings and reviews to decide whether they want to see this movie in the given cinema. Users can comment on other ratings.

#### 3.1.4 Favourites
To use favourites users have to be logged in. Movies and cinemas marked as favourites will be shown preferably and can be filtered in the view. 

#### 3.1.5 Filter
There will be certain ways to filter the list of movies and cinemas. Available filters are: 
- name
- location/distance
- favourites
- genre
- schedule
- prices

#### 3.1.6 Sorting
Information can be sorted by following criteria:
- name
- prices
- distance
- schedule (date and time)

#### 3.1.7 Reminders
To use reminders users have to be logged in. Users can set a reminder for movies. At the beginning of a new cinema week (traditionally thursday) users will receive an email or push notification that links to our user interface with preset filters.

#### 3.1.8 Locations
By retrieving the users current location we can filter the cinemas based on their distance. In case the user does not permit our application to retrieve his location there will be a way to enter his current position manually. There will also be a way to enter the maximum distance a user is willing to drive for the cinema. Location and maximum distance will be saved in the users account if available. 

#### 3.1.9 Database
In our database we will save user accounts and all information associated with them. Also all information about current movies, cinemas, schedules and prices will be stored.

#### 3.1.10 Receive data
Using a script we will update the information about movies, cinemas, schedule and prices daily. Those information will be stored in the database.

#### 3.1.11 Server
The server will dynamically generate the web interface as well as provide an API to retrieve information by the Android app. 

### 3.2 Usability
#### 3.2.1 Trainings
Both interfaces should be easy to use and there should not be any need for guides or trainings. Users just need to know how to open a website in a modern browser or how to use their smartphone.

#### 3.2.2 Common tasks
Users should be able to find the movies they are looking for within a maximum of 3 clicks.

### 3.3 Reliability
#### 3.3.1 Up-time
Our own server should ensure a 99% up-time. 

#### 3.3.2 Mean Time Between Failures
Between two failures we expect to be at least one week.

#### 3.3.3 Mean Time To Repair
After a failure the server should be up again in a maximum of four hours.

#### 3.3.4 Accuracy
As our application monitors dates and appointments we ensure 100% accuracy.

#### 3.3.5 Bugs
There should not be any critical bugs in our live system. Those bugs are supposed to be cought by our test systems. 
We expect to have 5-10 minor and significant bugs per thousand lines of code.
Critical bugs make our systems unusable and/or corrupts our data. Significant bugs affect single functions which might be unavailable as a result. 

### 3.4 Performance
#### 3.4.1 Response times
Loading the user interface should be completed in less than one second, therefor retrieving information from the data base need to be done in the same time. Filtering and showing information about a specific movie/cinema should also be completed in less than a second. 

#### 3.4.2 Capacity
The capacity depends on what the server we use can handle. We expect to be able to handle 1000 users at the same time. If we can not handle them, we need to expand our infrastructure. Therefor our infrastructure we will be designed to be expandable.

#### 3.4.3 Degradation modes
If we can not fetch any data from cinemas we can use our database up to one week so the system should not be impacted at all. After this week has passed users can still use all functionality of our system, except showing information about current prices and schedule.

If our servers are down, the application will not be available until servers are back up again. 

### 3.5 Supportability
#### 3.5.1 IDE and coding conventions
All developers can use the IDE they perfer to edit sourcecode. Developers have to ensure they are using the same sourcecode formatting and [codestyle][codestyle] as the others. The IDEs will probably be Eclipse and IntelliJ.

#### 3.5.2 Maintenance utilities
To access and maintain our database we will use phpMyAdmin. For maintenance work to the server we use whatever ssh client the developer prefers.

### 3.6 Design Constraints
#### 3.6.1 Language support
We will use the following languages, which will also be well supported in the future:

- Java EE 8
- Android 4.1+
- Internet Standards HTML, CSS and JavaScript

#### 3.6.2 Process requirements
For organizing the development process we will use Scrum and JIRA. The tool we will use for versioning control is GitHub.

#### 3.6.3 architectural and design constraints
The architecture of our application will be Spring MVC.

#### 3.6.4 Purchased components
The only purchased component is the server we are using.

#### 3.6.5 Class libraries
tbd

### 3.7 Online User Documentation and Help System Requirements
Our application will not require any help system or user documentation as it will be straight forward and easy to use.

### 3.8 Purchased Components
see 3.6.4

### 3.9 Interfaces
#### 3.9.1 User Interfaces
tbd

#### 3.9.2 Hardware Interfaces
(n/a)

#### 3.9.3 Software Interfaces
If available our system will use an API provided by cinemas to retrieve information about movies, schedule and prices.

For auto login with google accounts we will depend on the Google API.

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


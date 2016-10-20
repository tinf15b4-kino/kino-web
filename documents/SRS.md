# SmartCinema - Software Requirements Specification

## 1. Introduction
### 1.1 Purpose
This SRS describes all specifications for "SmartCinema". The aim of our application is to gather all information about all movie theaters including movies, prices, schedules, locations in one easily accessible place. Our product consists of an Android app and a webapplication. 

Our non functional requirements are that all information has to be up-to-date (synchronized between Android app and webapplication) and both interfaces should load quickly (<1 seconds). Our apps are supposed to be stable. Our aim is to have 99% up-time so all maintenance systems should not impact the running system.

### 1.2 Scope
This software specification applies to the whole "SmartCinema" application. Apart from the webapplication and the Android app we will have the following components:
- **Database** where we will store all information about our users and information extracted from movie theaters.
- **Data extraction script** which will fill and update the database.
- **MVC layer** which generates the web page(s) and provides information for the Android app.

### 1.3 Definitions, Acronyms and Abbreviations
In this section definitions and explanations of acronyms and abbreviations are listed to help the reader to understand these.

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
| [Overall Use Case Diagram (OUCD)](tbd)| 20.10.2016 |


### 1.5 Overview
The following chapters are about our vision and perspective, the software requirements, the demands we have, licensing and
the technical realisation of this project.

## 2. Overall Description
The aim of our application is to gather all information about all movie theaters in one easily accessible place.
Where can I watch my desired movie? When do I have to get there? How did other people think about it?
All those questions will be answered by our product. Get all those information on a website or check it out in our mobile app for android.

## 3. Specific Requirements
### 3.1 Functionality - Android App
#### 3.1.1 Settings
A settings option provides the user the possibility to choose the quality of the video and photo by himself. In this menu the user is also
able to change the server connection details. See also our separate [document][uc configure settings] for this use case.

#### 3.1.2 Switch user
On this page the user is able to type in his/her login information.
If somebody has multiple users and log in information, the application allows him to switch between them as well. To see the use case diagram
of this use case go to this [document][uc switch user].

#### 3.1.3 Take Picture
The user is able to take pictures with the smartphone camera. The picture will be instantly uploaded to a server.

#### 3.1.4 Take Video
The user is able to film with the smartphone camera. The captured video material is live streamed to a server to store the
data. There is a detached [document][uc capture video] describing this use case more precisely.

#### 3.1.5 Upload File
If the automatic upload of the video or picture failed, the user is able to upload the file manually. This use case is described in [this document][uc upload file].


### 3.2 Functionality - Website
#### 3.2.1 Register for a new account
At the home page of the website there is the possibility to register for a new user. A registration is approved by verifying the user e-mail address. For more information please consider [this document][uc approve registration].

#### 3.2.2 Login-Page
The website contains a login-form for users that have already registered for an account.

#### 3.2.3 Maintain Profile
This overview page allows the user to view and maintain his profile. A more detailed description can be found [here][uc switch user].

#### 3.2.4 Media Browser
At this page all captured and uploaded media of a single user is shown. He can browse the content easily.

#### 3.2.5 Content viewer
Each uploaded file is shown in the media browser. Detailed information is shown below and in a modal dialog the video can be watched. See [this document][uc view own media] for further information.

#### 3.2.6 Delete files
The user is able to delete his own media stored on the server. [Use case document][uc delete own media].

#### 3.2.7 Download files
Captured media can be downloaded only by the owner. Therefore he is able to select specific files in the media browser for download. [Use case document][uc download own media].

#### 3.2.8 Approve registration
Users have to verify their e-mail address to approve their account. [Use case document][uc approve registration].

#### 3.2.9 Manage Users
The administrator is able to perform several different functions on user data. You can read more about this use case [here][uc manage users].

#### 3.2.10 Manage Media
Inappropriate uploads can be deleted by the administrator.

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
tbd

### 3.5 Supportability
#### 3.5.1 Language support
We will use the following languages, which will also be well supported in the future:

- Java EE 8
- Android 4.1+
- Internet Standards HTML, CSS and JavaScript

#### 3.6.2 Support for dependencies
We will build our own RTSP streaming library compliant to [RFC 2326][] and [RFC 3550][] in Java for our backend. Therefore we can ensure that this library is compatible with our streaming application at any time. At client side we will use libstreaming that is hosted by Github. You can find the source code and the description at their [Github-Page][libstreaming]. This library may not be supported in the future, but at this time it will be possible to us our own streaming library in the Android app as well.

Our website frontend uses Angular and jQuery for displaying the media browser and managing the HTML DOM-manipulations.

### 3.7 Design Constraints
All information about the architectural design of our application stack can be found in our [software architecture document][sad]. In the following subchapters you can read about some generall important decisions.

#### 3.7.1 Backend in Java and PHP
The backend of this software should be written in PHP and Java. The PHP-stack is responsible for a RESTful API that is used both by our webinterface and by the Android application. The Java-stack implements a powerful streaming server that uses the RTP and RTSP protocol as already mentioned.

#### 3.7.2 MVC architecture
Our Android application should implement the MVC pattern.

### 3.8 On-line User Documentation and Help System Requirements
The whole application will be built with an intuitive design, so there shouldn’t be a need for the user to ask us or the program for help. However we will write our own blog, on which users can find information and ask us questions.

### 3.9 Purchased Components
(n/a)

### 3.10 Interfaces
#### 3.10.1 User Interfaces
Please consult the different use case descriptions for UI mockups (screenshots) and UI functionality descriptions:

- [UC1: Capture and stream video][uc capture video]
- [UC3: Maintain user profile][uc maintain profile]
- [UC2: Configure settigns][uc configure settings]
- [UC4: Switch user][uc switch user]
- [UC5: Register][uc register]
- [UC6: Browse own media][uc browse media]
- [UC7: Manage Users][uc manage users]
- [UC8: Delete own media][uc delete own media]
- [UC9: Download own media][uc download own media]
- [UC10: View own media][uc view own media]
- [UC11: Approve registration][uc approve registration]
- [UC12: Upload file][uc upload file]

#### 3.10.2 Hardware Interfaces
(n/a)

#### 3.10.3 Software Interfaces
(n/a)

#### 3.10.4 Communications Interfaces
(n/a)

### 3.11 Licensing Requirement
Our server side code is subject to the [Apache Licence 2.0](http://www.apache.org/licenses/LICENSE-2.0) as the libraries used in this application part.

### 3.12 Legal, Copyright and other Notices
(n/a)

### 3.13 Applicable Standards
RFCs:

- [RFC 3550][] - RTP: A Transport Protocol for Real-Time Applications
- [RFC 1889][] - RTP: A Transport Protocol for Real-Time Applications
- [RFC 2326][] - Real Time Streaming Protocol (RTSP)

## 4. Supporting Information
### 4.1 Appendices
You can find any internal linked sources in the chapter References (go to the top of this document). If you would like to know what the current status of this project is please visit the [Unveiled Blog][blog].


<!-- Link definitions: -->
[Edward Snowden]: http://www.brainyquote.com/quotes/quotes/e/edwardsnow551870.html
[Overall Use Case Diagram (OUCD)]: https://github.com/SAS-Systems/Unveiled-Documentation/blob/master/Bilder/UC_Diagrams/Unveiled_Overall%20Use%20Case%20Diagram.png "Link to Github"

[uc capture video]: http://unveiled.systemgrid.de/wp/docu/srs_uc1/ "Use Case 1: Capture and stream video"
[uc configure settings]: http://unveiled.systemgrid.de/wp/docu/srs_uc2/ "Use Case 2: Configure settings"
[uc maintain profile]: http://unveiled.systemgrid.de/wp/docu/srs_uc3/ "Use Case 3: Maintain profile"
[uc switch user]: http://unveiled.systemgrid.de/wp/docu/srs_uc4/ "Use Case 4: Switch user"
[uc register]: http://unveiled.systemgrid.de/wp/docu/srs_uc5/ "Use Case 5: Register"
[uc browse media]: http://unveiled.systemgrid.de/wp/docu/srs_uc6/ "Use Case 6: Browse own media"
[uc manage users]: http://unveiled.systemgrid.de/wp/docu/srs_uc7/ "Use Case 7: Manage users"
[uc delete own media]: http://unveiled.systemgrid.de/wp/docu/srs_uc8/ "Use Case 8: Delete own media"
[uc download own media]: http://unveiled.systemgrid.de/wp/docu/srs_uc9/ "Use Case 9: Download own media"
[uc view own media]: http://unveiled.systemgrid.de/wp/docu/srs_uc10/ "Use Case 10: View own media"
[uc approve registration]: http://unveiled.systemgrid.de/wp/docu/srs_uc11/ "Use Case 11: Approve registration"
[uc upload file]: http://unveiled.systemgrid.de/wp/docu/srs_uc12/ "Use Case 12: Upload file"

[sad]: http://unveiled.systemgrid.de/wp/docu/sad/ "Software Architecture Document"
[testplan]: http://unveiled.systemgrid.de/wp/docu/testplan/ "Testplan"
[blog]: http://unveiled.systemgrid.de/wp/blog/ "Unveiled Blog"
[website]: http://unveiled.systemgrid.de/ "Unveiled Website"
[jira]: http://jira.it.dh-karlsruhe.de:8080/secure/RapidBoard.jspa?rapidView=10&projectKey=UNV "Jira Unveiled Scrum Board"
[github]: https://github.com/SAS-Systems "Sourcecode hosted at Github"
[presentation]: https://github.com/SAS-Systems/Unveiled-Documentation/blob/master/Unveiled_Presentation_Final.pptx "Final project presentation"
[installation guide]: http://unveiled.systemgrid.de/wp/docu/installation/ "Android App Installation Guide"
[fpc]: http://unveiled.systemgrid.de/wp/docu/fpc/ "Function point calculation and use case estimation"

[RFC 3550]: https://tools.ietf.org/html/rfc3550
[RFC 2326]: https://tools.ietf.org/html/rfc2326
[RFC 1889]: https://www.ietf.org/rfc/rfc1889.txt

[libstreaming]: https://github.com/fyhertz/libstreaming

<!-- Picture-Link definitions: -->
[OUCD]: https://raw.githubusercontent.com/SAS-Systems/Unveiled-Documentation/master/Bilder/UC_Diagrams/Unveiled_Overall%20Use%20Case%20Diagram.png "Overall Use Case Diagram"
[class diagram php]: https://raw.githubusercontent.com/SAS-Systems/Unveiled-Documentation/master/Bilder/UML%20Class%20diagrams/UML-PHP-Stack_new.png "Class Diagram for our Backend PHP-Stack"
[deployment diagram]: https://raw.githubusercontent.com/SAS-Systems/Unveiled-Documentation/master/Bilder/UML%20Class%20diagrams/UML_deployment.png "Deployment diagram, shows all modules and the relations between them"
[ci lifecycle]: https://raw.githubusercontent.com/SAS-Systems/Unveiled-Documentation/master/Bilder/auto_deployment_lifecycle.png "Continuous Integration process"

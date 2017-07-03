# SmartCinema - Test Plan

## 1. Introduction

### 1.1 Purpose
The purpose of the Iteration Test Plan is to gather all of the information necessary to plan and control the test effort for a given iteration. It describes the approach to testing the software, and is the top-level plan generated and used by managers to direct the test effort.
This Test Plan for the SmartCinema supports the following objectives:
- Controllers
- Models
- Views
- REST-Api
- Android App

### 1.2 Scope
Testing is done with user interface, function and load tests.

### 1.3 Intended Audience
This document is intended for the use of Team SmartCinema as well as the project's stakeholders.

### 1.4 Document Terminology and Acronyms
n/a

### 1.5 References
n/a

### 1.6 Document Structure
n/a

## 2. Evaluation Mission and Test Motivation

### 2.1 Background
Testing is very important part of developing software. We define our testing rules in this document to make sure that
- testing will not be forgotten
- all team members understand the importance of testing
- project outsiders can easily get an overview about our testing

### 2.2 Evaluation Mission
- find as many bugs as possible
- find important problems, assess perceived quality risks
- advise about perceived project risks
- certify to a standard
- verify a specification (requirements, design or claims)
- advise about product quality, satisfy stakeholders
- advise about testing
- fulfill process mandates 

### 2.3 Test Motivators
Using continued testing techniques helps us to provide a stable and correct working product, according to customer and/or internal specification. 


## 3. Target Test Items
The listing below identifies those test items - software, hardware, and supporting product elements - that have been identified as targets for testing. This list represents what items will be tested. 
- Controllers
- Models
- Views
- REST-Api
- Android App

## 4. Outline of Planned Tests

### 4.1 Outline of Test Inclusions
- User interface testing
- Function Testing
- Load testing

### 4.2 Outline of other candidates for potential inclusion
n/a

### 4.3 Outline of Test Exclusions
n/a

## 5. Test Approach

### 5.1 Initial Test-Idea Catalogs and other reference sources
n/a

### 5.2 Testing Techniques and Types

#### 5.2.1 Data and Database Integrity Testing
n/a

#### 5.2.2 Function Testing
| | |
|-|-|
| **Technique Objective:** | Exercise target-of-test functionality, including navigation, data entry, processing, and retrieval to observe and log target behavior. |
| **Technique:** | Execute each use-case scenario's individual use-case flows or functions and features, using valid and invalid data, to verify that:  <br>- the expected results occur when valid data is used <br> - the appropriate error or warning messages are displayed when 	invalid data is used <br> - each business rule is properly applied |
| **Oracles:** | We assume  all tests to pass. |
| **Required Tools:** | - Selenium Driver (currently selenium 3.4 with geckodriver 0.16 and Firefox 54. We follow the Firefox releases closely to avoid unexpected regressions caused by browser updates.) <br> - Cucumber extension for Eclipse/IntelliJ |
| **Success Criteria:** | A cucumber file for each Use-Case that test every possible path. |
| **Special Considerations:** | n/a |


#### 5.2.3 Business Cycle Testing
n/a

#### 5.2.4 User Interface Testing
n/a

#### 5.2.5 Performance Profiling
n/a

#### 5.2.6 Load Testing
| | |
|-|-|
| **Technique Objective:** | Verify appropriate behavior and response time under high load. |
| **Technique:** | Swamp endpoints with concurrent requests. Observe CPU load on server and measure response times. Commands are entered manually on the console or can alternatively be automated with a shell script. |
| **Oracles:** | We assume that the endpoints we test actually do the advertised work. |
| **Required Tools:** | - Apache Bench tool `ab` (as shipped with Apache 2.4) |
| **Success Criteria:** | `ab` reports success responses only with mean response time < 100ms |
| **Special Considerations:** | n/a |

#### 5.2.7 Stress Testing
n/a

#### 5.2.8 Volume Testing
n/a

#### 5.2.9 Security and Access Control Testing
n/a

#### 5.2.10 Failover and Recovery**tbd/WHATEVER** Testing
n/a

#### 5.2.11 Configuration Testing
n/a

#### 5.2.12 Installation Testing
n/a

#### 5.2.13 Unit Testing

|                         |                                                     |
|-------------------------|-----------------------------------------------------|
| Technique Objective:    | Testing the functionality of the code               |
| Technique:              | Test using unit tests.                              |
| Oracles:                | The tests are successful if all assertions are true |
| Required Tools:         | JUnit 4                                             | 
| Success Criteria:       | All tests pass                                      |
| Special Considerations: |                                                     |

## 6. Entry and Exit Criteria

### 6.1 Test Plan

#### 6.1.1 Test Plan Entry Criteria
n/a

#### 6.1.2 Test Plan Exit Criteria
n/a

#### 6.1.3 Suspension and resumption criteria
n/a

### 6.2 Test Cycles

#### 6.2.1 Test Cycle Entry Criteria
n/a

#### 6.2.2 Test Cycle Exit Criteria
n/a

#### 6.2.3 Test Cycle abnormal termination
n/a


## 7. Deliverables

### 7.1 Test Evaluation Summaries
n/a

### 7.2 Reporting on Test Coverage
**tbd**

### 7.3 Perceived Quality Reports
n/a

### 7.4 Incident Logs and Change Requests
Test results are displayed in the SmartCinema Slack project. This ensure that failed test will recognized immediately by the team.

### 7.5 Smoke Test Suite and supporting Test Scripts
n/a

### 7.6 Additional work products
n/a

#### 7.6.1 Detailed Test Results
n/a

#### 7.6.2 Additional automated functional Test Scripts
**tbd** whatever this should be
[These will be either a collection of the source code files for automated test scripts, or the repository of both source code and compiled executables for test scripts maintained by the test automation product.]

#### 7.6.3 Test Guidelines
n/a

#### 7.6.4 Traceability Matrices
n/a


## 8. Testing Workflow
For continuous integration we set up a Jenkins-Server that creates a build for every branch in the corresponding GIT repository.
Everytime a commit is pushed to the server Jenkins execute the defined tests and display the results.
For example:
- [Test results master](https://jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/master/lastSuccessfulBuild/testReport/)
- [Test results develop](https://jenkins.genosse-einhorn.de/job/SmartCinema%20Web%20App%20MULTIBRANCH/job/develop/lastSuccessfulBuild/testReport/)

Also we integrated Sonarqube into our testing workflow. Every push onto the develop-branch will trigger a Sonarqube update. On our [Sonarqube-Page](https://sonarqube.com/dashboard?id=de.tinf15b4.kino%3AHEAD) are many information and metrics related to our code provided. 

## 9. Environmental Needs

### 9.1 Base System Hardware
| Resource | Quantity | Name and Type |
|----------|----------|---------------|
| Web Server | 1 | small VPS rented from https://www.netcup.de/ |
| - Server Name | | alicia.genosse-einhorn.de |
| Data API Server | 1 | N/A (shares VPS with web server)
| Build Server | 1 | N/A (shares VPS with web server) |

### 9.2 Base Software Elements in the Test Environment
| Software Element Name | Version | Type and Other Notes |
|-----------------------|---------|----------------------|
| CentOS (like RHEL) | 7.3 | Operating System / Linux Distribution |
| Firefox | 54 | Internet Browser |
| Selenium | 3.4, with geckodriver 0.16 | Browser automation tool |

### 9.3 Productivity and Support Tools
| Tool Category or Type | Tool Brand Name | Vendor or In-house | Version |
|-----------------------|-----------------|--------------------|---------|
| Project Management | JIRA | Atlassian | N/A (hosted by DHBW) |
| Source Code Hosting | GitHub | GitHub, Inc. | N/A (public web app) |
| Instant Messager | Slack | Slack Technologies | N/A (public web app) |


### 9.4 Test Environment Configurations
n/a

## 10. Responsibilities, Staffing and Training Needs

### 10.1 People and Roles
As we already announced in our blog post ["RUP roles and technology"](https://smartcinemaproject.wordpress.com/2016/10/13/rup-roles/) every member of Team SmartCinema is Implementer and Test Designer. We all code and write tests for it.

### 10.2 Staffing and Training Needs
n/a


## 11. Iteration Milestones
| Milestone | Planned Start Date | Actual Start Date | Planned End Date | Actual End Date |
|-----------|--------------------|-------------------|------------------|-----------------|
| Iteration starts | 25.04.2017 | | | |
| > 20% Test Coverage | 25.04.2017 | 25.04.2017 | 21.06.2017 | 21.06.2017 |
| Have Functional Tests | 25.04.2017 | 25.04.2017 | 21.06.2017 | 21.06.2017 |
| Have Unit Tests | 25.04.2017 | 25.04.2017 | 21.06.2017 | 21.06.2017 |
| Have Load Tests | 25.04.2017 | 25.04.2017 | 21.06.2017 | 21.06.2017 |
| Tests integrated in CI | 25.04.2017 | 25.04.2017 | 21.06.2017 | 21.06.2017 |
| Iteration ends | | | 21.06.2017 | |


## 12. Risks, Dependencies, Assumptions and Constraints
| Risk | Mitigation Strategy | Contingency (Risk is realized) |
|------|---------------------|--------------------------------|
| Test data proves to be inadequate. | Tester will indicate what is required and will ensure to use a full set of suitable and protected test data. | Redefine test data |
| Database is corrupt | Database-Admin will endeavor to keep data clean and make regularly full database backups. | Restore database |

## 13. Management Process and Procedures

### 13.1 Measuring and Assessing the Extent of Testing
n/a

### 13.2 Assessing the deliverables of this Test Plan
n/a

### 13.3 Problem Reporting, Escalation and Issue Resolution
n/a

### 13.4 Managing Test Cycles
n/a

### 13.5 Traceability Strategies
n/a

### 13.6 Approval and Signoff
n/a

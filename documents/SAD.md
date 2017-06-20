# SmartCinema
# Software Architecture Document


## 1. Introduction

### 1.1 Purpose
This document provides a comprehensive architectural overview of the system, using a number of different architectural views to depict different aspects of the system. It is intended to capture and convey the significant architectural decisions which have been made on the system.

### 1.2 Scope
This project shows the architecture of our project SmartCinema.

### 1.3 Definitions, Acronyms, and Abbreviations
| Acronym | |
|---------|---|
| API | Application Programming Interface |
| ERM | Entity Relationship Modell |
| MVC | Modell View Controller|

### 1.4 References
n/a

### 1.5 Overview
n/a

## 2. Architectural Representation 
![MVC][mvc]

## 3. Architectural Goals and Constraints 
We decided to use the [Spring MVC](https://spring.io/).
The following picuture gives a shor overview of Spring MVC processing sequence.
![spring][spring]
Quelle: http://terasolunaorg.github.io/guideline/1.0.1.RELEASE/en/Overview/SpringMVCOverview.html

## 4. Use-Case View
n/a
### 4.1 Use-Case Realizations


## 5. Logical View 

### 5.1 Overview
![UML][uml]


## 6. Process View 
n/a

## 7. Deployment View 
![software architecture][sa]

## 8. Implementation View 

### 8.1 Overview
n/a
### 8.2 Layers
n/a

## 9. Data View (optional)
![entity relationship modell][erm]

## 10. Size and Performance 
n/a

## 11. Quality 
n/a


<!-- Link definitions: -->
[sa]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/software_architecture/software_architecture.png
[mvc]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/software_architecture/mvc.png
[spring]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/software_architecture/spring.png
[erm]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/database/erm_modell.png
[uml]: https://github.com/tinf15b4-kino/kino-web/blob/develop/documents/UML/uml_MVC.jpg

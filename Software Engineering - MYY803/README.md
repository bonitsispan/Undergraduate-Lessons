# Lesson Description

This course focuses on issues related to software lifecycle in general and to the individual phases that constitute the software lifecycle: requirements (elicitation, analysis, documentation, quality), design (architecture and technical design, documentation, quality, architectural styles), implementation, testing (unit, integration, system testing), delivery (user training, documentation).

# Project Description

This project is a web-based application developed in Java that enables students to browse available diploma thesis subjects offered by professors, apply for subjects they are interested in, and track their application status. Professors can create, edit, and delete diploma thesis subjects, review applications submitted by students, assign subjects based on different strategies, and evaluate the final outcomes through a grading system.

The application follows a layered architecture ensuring modularity and maintainability, consisting of DAO, Service, Controller, Model, and Config layers. It implements the Model-View-Controller (MVC) pattern using the Spring Boot framework, promoting a clean separation of concerns between data handling, business logic, and user interface.

Multiple subject assignment strategies are supported, including random assignment, assignment based on the best average grade, fewest remaining courses, and custom thresholds. The flexibility of the assignment mechanism is achieved by applying well-known design patterns, such as Strategy and Template Method, making the system easy to maintain and extend.

Key technologies used include Java 8+, Spring Boot, Spring MVC, MySQL for the database, and Thymeleaf for the web interface. Testing was performed using JUnit and Mockito frameworks. The project demonstrates skills in full-stack software development, application architecture, and the practical application of design patterns in a real-world academic system.

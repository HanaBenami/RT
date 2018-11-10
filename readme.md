# RT web application

A web application written in Java, using Spring Boot 2.0.1 with Tomcat as the servlet container.
The fronted UI is written using Vaadin 8, with Vaadin Spring extension.
The

##Application Functionality
The application is designed to be an internal website:
managing customers details
opening tickets for service calls
assigning work schedule for drivers and employees
monitoring open service calls
etc...

##Databases and profiles
The application consists of few different spring and maven profiles:
"Demo" (Spring) + "H2" (Maven): The demo profile using H2 in memory database, with initial data created when starting the application (using the files "schema.sql" and "data.sql").
"Prod" (Spring) + "MSSQL" (Maven): The production profile using an on premise MSSQL server.
"Dev" (Spring) + "MSSQL" (Maven): The development profile using an on premise MSSQL server and debugging mode enabled for Vaadin framework.

##How to run the demo?

:link: Run the code and go to [http://localhost:8090/](http://localhost:8090/)


JAVA -Dfile.encoding=UTF8 -jar rt-2.2.jar
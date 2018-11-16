# RT web application
[![Build Status](https://travis-ci.org/HanaBenami/RT.svg?branch=master)](https://travis-ci.org/HanaBenami/RT)
[![Heroku](http://heroku-badge.herokuapp.com/?app=hana2019&style=flat&svg=1)](https://hana2019.herokuapp.com/)

A web application written in Java, using Spring Boot 2.0.1 with Tomcat as the servlet container.
The fronted UI is written using Vaadin 8, with Vaadin Spring extension.

## Application Functionality
- [x] The application is designed to be an internal website:
- [x] Managing customers details
- [x] Creating tickets for service calls
- [x] Assigning work schedule for drivers and employees
- [x] Monitoring open service calls

## Databases and profiles
The application consists of few different spring and maven profiles:
* **"Demo"** (Spring) + **"H2"** (Maven): The demo profile using H2 in memory database, with initial data created when starting the application (using the files "schema.sql" and "data.sql").
* **"Prod"** (Spring) + **"MSSQL"** (Maven): The production profile using an on premise MSSQL server.
* **"Dev"** (Spring) + **"MSSQL"** (Maven): The development profile using an on premise MSSQL server and debugging mode enabled for Vaadin framework.

## Online demo
:link: [https://hana2019.herokuapp.com/](https://hana2019.herokuapp.com/) <br/>
(It might take a minute to load the application for the first time, since the application is sleeping if there is no traffic for 30 minutes)

## License
:copyright: Hana Ben-Ami

:email: [Contact me](mailto:hana.benami@gmail.com)

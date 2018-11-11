# RT web application

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

## How to run the demo?
1. **Download**: :link:[RT.jar](https://drive.google.com/file/d/1SEB9qbpnZ24JYBqUHcheoSdYe-Y1Ubrc/view?usp=sharing)
2. **Run**: JAVA -Dfile.encoding=UTF8 -jar rt.jar
3. **Go to**: :link::link:[http://localhost:8090/](http://localhost:8090/){:target="_blank"}

## License
:copyright: Hana Ben-Ami

:email: [Contact me](mailto:hana.benami@gmail.com)

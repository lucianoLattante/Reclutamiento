# Recruitment application

Educational team project for employer and candidate profiles, built with Java 8, Spring Boot 2.5.5, Spring MVC/Thymeleaf, JPA, Spring Security and MySQL. This is an earlier collaborative learning project, not a production recruitment platform.

## Team

Tomas Casabona, Luciano Lattante, Cintia Avendanio, Brunela Scaloni, Gabriel Soda and Esteban Gianotti. This repository represents the team's work; individual contributions must be attributed using commit evidence.

## Local setup

Create a disposable MySQL database named `reclutamiento`, export DB_USERNAME and DB_PASSWORD, then run `mvn clean spring-boot:run` from source. Open http://localhost:8080. See [.env.example](.env.example) for configuration names; Spring Boot does not automatically load that file.

Mail defaults to a loopback SMTP sink at 127.0.0.1:1025 without credentials or TLS. External SMTP must be explicitly configured. Do not use live mail credentials for tests.

These are source-derived instructions, not a verified end-to-end startup result. The schema currently uses Hibernate update; migrations and disposable integration tests remain future work.

## Security and reproducibility

Source configuration and its tracked target/classes copy have been changed to environment placeholders in this proposal. Previously exposed credentials still require owner revocation and historical review. The tracked target JAR and class files remain unaudited: do not run or redistribute them; rebuild from source. No artifacts were deleted and no history was rewritten.

Configuration placeholder checks and documentation review were performed; application startup, database integration and runtime tests were not executed. CSRF, route authorization, dependency updates and historical/binary secret scanning remain open.

## Original project instructions

Proyecto reclutamiento de empleados hecho por: 
Tomas Casabona,Luciano Lattante,Cintia Avendanio,Brunela Scaloni,Gabriel Soda,Esteban Gianotti

1-clonarlo 2-crear una base de datos llamada "reclutamiento" y buildearlo 3-correrlo 4-ingrese a 'http://localhost:8080/' para probarlo


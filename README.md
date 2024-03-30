# Bookstore API

## gRPC Reactive API
Application implements reactive gRPC CRUD operations on books. <br>
BookService and messages are defined in [main.proto](./src/main/proto/main.proto) file configuration.

## Prerequisites
Before running application and tests:
- install and run Docker on your local machine
- execute `gradlew spotlessApply` to format code according to Spotless code style

## Running
To run this application:
1. execute `gradlew clean assemble`
2. execute `docker compose up` on your docker host  

Application is working on [localhost:9090](http://localhost:9090)

## Testing 
Unit and integration tests are separated in different modules:
- To run unit tests execute `gradlew test`
- Integration tests implemented with Citrus Framework and Testcontainers. <br>
To run integration tests execute `gradlew citrusTest` <br>  

Citrus tests with testcontainers don`t run correctly on Windows. <br>
Make sure that integration tests are running on Linux OS.
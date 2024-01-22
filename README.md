# Sharing Service
This is a sharing service, which will allow users to tell what 
things they want to share, to find the right thing and rent it 
for a while. The service also close access to it at the time of 
booking from other people. In case there is no necessary thing 
on the service, users are able to leave requests.

It consists of two services:
- validation service
- the main service for the product operation

This is RESTful API that works with the Postgres database and 
is covered with Unit-tests.

## Content
- [Stack](#stack)
- [Usage](#usage)
- [Sources](#sources)

## Stack
- Java 11
- Spring Boot
- Maven
- Postgres:13.7-alpine
- Spring Data JPA

## Usage
- Download this repository
- Execute the commands
```sh
mvn clean install
```
```sh
docker-compose build
```
```sh
docker-compose up
```
- The validation service is available at: http://localhost:8080.
  The main service is available at: http://localhost:9090

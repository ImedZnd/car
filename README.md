# Car
Car has been created in purpose of learning the best practices in software engineering.

[![Coverage Status](https://coveralls.io/repos/github/ImedZnd/car/badge.svg?branch=dev)](https://coveralls.io/github/ImedZnd/car?branch=dev)
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) sample app.

## Requirements

For building and running the application you need:

- [JDK 17](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [vavr 1.0.0-alpha-4](https://docs.vavr.io/)
- [TNG/archunit 0.23.0](https://github.com/TNG/ArchUnit)
- [Lombok](https://projectlombok.org/)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.CleancarcrudApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```
### Features

- [x] REST Api
- [x] Unit Tests
- [ ] Pipeline
- [ ] Integrat Kafka Broker 
- [ ] Test Kafka
- [ ] Add Database

## Features

- Implementing [SOLID][SOLID] principles
- Don't repeat yourself (DRY)
- GRASP
- KISS principle
- Export documents as Markdown, HTML and PDF


## API ARCHITECTURE

| RESOURCES	 | URL(PATH) | METHOD | PARAMETERS |
| ------ | ------ | ------ | ------ |
| Save Car |"/save"|POST| plateNumber, type , release date |
| Get All Cars | "/" |GET|------ |
| Get Car By Plate Number | "/plateNumber/{plateNumber}" |GET|plateNumber |
| Get Cars By Type | "/type/{type}" |GET|type |
| Get Cars By Release Year |"/releaseYear/{releaseYear}"|GET|release year |
| Update Car |"/update"|PUT|plateNumber, type , release date|
| Delete Car | "/delete"|POST|plateNumber, type , release date |
| Delete All Cars | "/deleteAll"|DELETE|------ |

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./mvn clean test

# Contribute ?

fork and PR to improve the project.

## License

Released under the MIT 

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [SOLID]: <https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html>




# Hiccup application

This is a simple application that try to simulate player for **Dragons of mugloar** game.
The application is named in honor of the main character from **How to train dragon** movie.

## Getting Started

Project use gradle for build and other repetitive tasks. To build project you need to execute:

```shell
./gradlew build
```

If you want to run application you can either:

```shell
./gradlew bootRun -maxTurn 100
```

or:

```shell
java -jar ./build/libs/hiccup-0.0.1-SNAPSHOT.jar -maxTurn 100
```

If you want to know what other options exist for this application:

```shell
java -jar ./build/libs/hiccup-0.0.1-SNAPSHOT.jar -help
```
## Development

I'm using unit tests,  check style and sonar to ensure code quality.

To run tests:

```shell
./gradlew test # you can run ./gradlew check as well
```

To run checkstyle:

```shell
./gradlew checkstykeMain
```

To run sonar:

```shell
docker-compose up -d -f ./src/main/docker/sonar.yml
./gradlew sonarqube
```

Of course, you can just import project in IntelliJ IDEA and use UI to start mentioned gradle task earlier.

## Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.3/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.3/gradle-plugin/reference/html/#build-image)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#configuration-metadata-annotation-processor)

## Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)


# logback appender for javabrake

[![Build Status](https://travis-ci.org/airbrake/logback.svg?branch=master)](https://travis-ci.org/airbrake/logback)

## Introduction

logback is a logging Middleware in Java for Airbrake.

## Installation

Gradle:

```gradle
compile 'io.airbrake:logback:0.1.2'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>logback</artifactId>
  <version>0.1.2</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='logback' rev='0.1.2'>
  <artifact name='logback' ext='pom'></artifact>
</dependency>
```

## Configuration
If you want to send the error logs to Airbrake, you need to have following lines in logback.xml. Add this file in the resources folder. This is the main file for logback configuration. and contains information about log levels, log appenders.

```xml
<configuration>
  <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="Airbrake" class="io.airbrake.logback.AirbrakeAppender">
    <projectId>12345</projectId>
    <projectKey>FIXME</projectKey>
    <env>production</env>
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>ERROR</level>
    </filter>
  </appender>

  <root level="INFO">
    <appender-ref ref="Console" />
    <appender-ref ref="Airbrake" />
  </root>
</configuration>
```
## Error Logging

```java
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

Logger logger = ((ch.qos.logback.classic.Logger))LoggerFactory.getLogger("Name");

try {
  do();
} catch (IOException e) {
  logger.error(e.getMessage());
}
```

## Notifier release instrucitons

### A note on Java version
Make sure you build and release this notifier with open-jdk, one way to manage your local java version is using [asdf](https://asdf-vm.com). You can install this tool via homebrew:
```
brew install asdf
```
Then install open-jdk-'mention version here' and set it as JAVA home before running any of the `./gradlew` commands:
```
asdf plugin add java
asdf install java openjdk-'mention version here'
export JAVA_HOME=$HOME/.asdf/installs/java/openjdk-'mention version here'
```

### Building and Releasing

```shell
./gradlew build
```
Upload to Maven Central:

```shell
./gradlew publish
```

To release the deployment to maven central repository:
 - http://central.sonatype.org/pages/releasing-the-deployment.html

Usefull links:
 - https://search.maven.org/artifact/io.airbrake/logback
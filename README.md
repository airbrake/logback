# logback appender for javabrake

[![Build Status](https://travis-ci.org/airbrake/logback.svg?branch=master)](https://travis-ci.org/airbrake/logback)

## Installation

Gradle:

```gradle
compile 'io.airbrake:logback:0.1.0'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>logback</artifactId>
  <version>0.1.0</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='logback' rev='0.1.0'>
  <artifact name='logback' ext='pom'></artifact>
</dependency>
```

## Configuration

```xml
<configuration>
  <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="Airbrake" class="io.airbrake.logback.AirbrakeAppender">
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

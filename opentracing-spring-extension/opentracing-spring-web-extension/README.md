# OpenTracing: Spring Web Extension

[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
![Build Status](https://github.com/zalando/opentracing-toolbox/workflows/Test/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/opentracing-toolbox/master.svg)](https://coveralls.io/r/zalando/opentracing-toolbox)
[![Code Quality](https://img.shields.io/codacy/grade/69e173024eec403797466e147a2051a3/master.svg)](https://www.codacy.com/app/whiskeysierra/opentracing-toolbox)
[![Javadoc](http://javadoc.io/badge/org.zalando/opentracing-spring-web-extension.svg)](http://www.javadoc.io/doc/org.zalando/opentracing-spring-web-extension)
[![Release](https://img.shields.io/github/release/zalando/opentracing-toolbox.svg)](https://github.com/zalando/opentracing-toolbox/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/opentracing-spring-web-extension.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/opentracing-spring-web-extension)
[![OpenTracing](https://img.shields.io/badge/OpenTracing-enabled-blue.svg)](http://opentracing.io)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/opentracing-toolbox/master/LICENSE)

- **Technology stack**: Java 8+, OpenTracing, Spring
- **Status**: Under development and used in production

## Example

```java
registry.addInterceptor(new TracingHandlerInterceptor(
        tracer, singletonList(new StandardSpanDecorator())));
```

## Features

- applies Zalando's standard tags/logs to each request
- builds on top of an existing instrumentation (see [dependencies](#dependencies))

## Dependencies

- Java 8 or higher
- [OpenTracing](https://github.com/opentracing/opentracing-java)
- [OpenTracing Java Web Servlet Filter Instrumentation](https://github.com/opentracing-contrib/java-web-servlet-filter)
   - The Spring Web extension will only augment the span that the servlet filter already created 
- [OpenTracing Spring Web Instrumentation](https://github.com/opentracing-contrib/java-spring-web)

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-spring-web-extension</artifactId>
    <version>${opentracing-spring-web-extension.version}</version>
</dependency>
```

A new span will be started for each request/response. 

The following tags/logs are supported out of the box:

| Tag/Log Field          | Decorator                     | Example                           |
|------------------------|-------------------------------|-----------------------------------|
| `http.path`            | `HttpPathSpanDecorator`       | `/users`                          |
| `error`                | `ErrorSpanDecorator`          | `true`                            |
| `error.kind` (log)     | `ErrorSpanDecorator`          | `SocketTimeoutException`          |
| `error.object` (log)   | `ErrorSpanDecorator`          | (exception instance)              |
| `message` (log)        | `ErrorMessageSpanDecorator`   | `Connection timed out`            |
| `stack` (log)          | `ErrorStackSpanDecorator`     | `SocketTimeoutException at [...]` |

Custom `SpanDecorator` implementations that are registered using [Java's Service Provider Interface](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) mechanism will be picked up automatically by default.

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

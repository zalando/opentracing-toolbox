# OpenTracing: JDBC

[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
![Build Status](https://github.com/zalando/opentracing-toolbox/workflows/Test/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/opentracing-toolbox/main.svg)](https://coveralls.io/r/zalando/opentracing-toolbox)
[![Code Quality](https://img.shields.io/codacy/grade/69e173024eec403797466e147a2051a3/main.svg)](https://www.codacy.com/app/whiskeysierra/opentracing-toolbox)
[![Javadoc](http://javadoc.io/badge/org.zalando/opentracing-jdbc.svg)](http://www.javadoc.io/doc/org.zalando/opentracing-jdbc)
[![Release](https://img.shields.io/github/release/zalando/opentracing-toolbox.svg)](https://github.com/zalando/opentracing-toolbox/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/opentracing-jdbc.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/opentracing-jdbc)
[![OpenTracing](https://img.shields.io/badge/OpenTracing-enabled-blue.svg)](http://opentracing.io)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/opentracing-toolbox/main/LICENSE)

- **Technology stack**: Java 8+, OpenTracing, JDBC
- **Status**: Under development and used in production

## Why not [opentracing-contrib/java-jdbc](https://github.com/opentracing-contrib/java-jdbc):question: 

- operation name not customizable
- span tags not customizable
- `java.sql.Driver` approach requires `GlobalTracer`
- no support for `javax.sql.DataSource`

## Example

```java
DataSource dataSource = new DataSourceTracer(tracer)
        .trace(originalDataSource);
```

## Features

- OpenTracing instrumentation for any `DataSource`
- Customizable operation name
- Customizable span tags/logs
- Convention over configuration (i.e. meaningful default values)

## Dependencies

- Java 8 or higher
- [OpenTracing](https://github.com/opentracing/opentracing-java)
- [datasource-proxy](https://github.com/ttddyy/datasource-proxy)

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-jdbc</artifactId>
    <version>${opentracing-jdbc.version}</version>
</dependency>
```

## Configuration

```java
new DataSourceTracer(tracer)
    .withOperationName(new CustomOperationName())
    .withAdditionalSpanDecorator(new CustomSpanDecorator());
```

A new span will be started for each statement. 

The following tags/logs are supported out of the box:

| Tag/Log Field        | Decorator                        | Example                           |
|----------------------|----------------------------------|-----------------------------------|
| `component`          | `ComponentSpanDecorator`         | `JDBC`                            |
| `db.instance`        | `DatabaseInstanceSpanDecorator`  | `db`                              |
| `db.statement`       | `DatabaseStatementSpanDecorator` | `SELECT * FROM user WHERE id = ?` |
| `db.type`            | `DatabaseTypeSpanDecorator`      | `sql`                             |
| `db.user`            | `DatabaseUserSpanDecorator`      | `root`                            |
| `peer.address`¹      | `PeerAddressSpanDecorator`       | `postgres://localhost`            |
| `peer.hostname`      | `PeerSpanDecorator`              | `localhost`                       |
| `peer.ipv4`          | `PeerSpanDecorator`              | `127.0.0.1`                       |
| `peer.ipv6`          | `PeerSpanDecorator`              | `::1`                             |
| `peer.port`          | `PeerSpanDecorator`              | `5432`                            |
| `error`              | `ErrorSpanDecorator`             | `true`                            |
| `error.kind` (log)   | `ErrorSpanDecorator`             | `SocketTimeoutException`          |
| `error.object` (log) | `ErrorSpanDecorator`             | (exception instance)              |
| `message` (log)      | `ErrorMessageSpanDecorator`      | `Connection timed out`            |
| `stack` (log)        | `ErrorStackSpanDecorator`        | `SocketTimeoutException at [...]` |

¹ Disabled by default due to security concerns (may expose passwords)

Custom `SpanDecorator` implementations that are registered using [Java's Service Provider Interface](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) mechanism will be picked up automatically by default.

### Operation Name

The operation name, by default, is derived from the `Statement`'s method that was used to execute it. Usually one of `execute`, `update`, `executeQuery`, `executeUpdate`, `executeBatch`, etc.

The `OperationName` interface can be implemented in order to customize the operation name:

```java
new DataSourceTracer(tracer)
    .withOperationName((method, queries) -> 
        toSnakeCase(method.getName()));
```

Another alternative that is built-in is the `StoredProcedureOperationName` that
looks for queries with the format `SELECT * FROM my_function(..)` and will
extract the name of the function as the operation name.

```java
new DataSourceTracer(tracer)
    .withOperationName(new StoredProcedureOperationName());
```

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

# OpenTracing FlowID

![Build Status](https://github.com/zalando/opentracing-toolbox/workflows/Test/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/opentracing-toolbox/master.svg)](https://coveralls.io/r/zalando/opentracing-toolbox)
[![Code Quality](https://img.shields.io/codacy/grade/213bb62c41b34a32951929e37a2d20ac/master.svg)](https://www.codacy.com/app/whiskeysierra/opentracing-toolbox)
[![Javadoc](http://javadoc.io/badge/org.zalando/opentracing-flowid-core.svg)](http://www.javadoc.io/doc/org.zalando/opentracing-flowid-core)
[![Release](https://img.shields.io/github/release/zalando/opentracing-toolbox.svg)](https://github.com/zalando/opentracing-toolbox/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/opentracing-toolbox.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/opentracing-toolbox)
[![OpenTracing](https://img.shields.io/badge/OpenTracing-enabled-blue.svg)](http://opentracing.io)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/opentracing-toolbox/master/LICENSE)

*OpenTracing FlowID* is a library that builds on top of [OpenTracing](https://opentracing.io/) and adds support for our legacy 
[`X-Flow-ID`](https://opensource.zalando.com/restful-api-guidelines/#233) header.

- **Status**: Under development and used in production

## Origin

This library historically originates from a closed-source implementation called *Flow-ID*. The goal was to create a clean open source version in which we could get rid of all the drawbacks of the old implementation, e.g. strong-coupling to internal libraries and limited testability.

## Features

- **OpenTracing** extensions
   - support for legacy [`X-Flow-ID`](https://opensource.zalando.com/restful-api-guidelines/#233) propagation
-  **Support** for Servlet containers, Apache’s HTTP client, Square's OkHttp and (via its elegant API) several other frameworks
-  Convenient [Spring Boot](http://projects.spring.io/spring-boot/) Auto Configuration
-  Sensible defaults

## Dependencies

- Java 8
- Any build tool using Maven Central, or direct download
- [OpenTracing](https://opentracing.io/guides/java/) 0.32.0 or higher
- Servlet Container (optional)
- Apache HTTP Client (optional)
- OkHttp (optional)
- Spring 4.x **or 5.x** (optional)
- Spring Boot 1.x **or 2.x** (optional)

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-core</artifactId>
    <version>${opentracing-toolbox.version}</version>
</dependency>
```

Additional modules/artifacts of Tracer always share the same version number.

Alternatively, you can import our *bill of materials*...

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.zalando</groupId>
      <artifactId>opentracing-flowid-bom</artifactId>
      <version>${opentracing-toolbox.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

... which allows you to omit versions and scopes:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-core</artifactId>
</dependency>
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-servlet</artifactId>
</dependency>
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-httpclient</artifactId>
</dependency>
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-okhttp</artifactId>
</dependency>
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-flowid-spring-boot-starter</artifactId>
</dependency>
```

## Usage

After adding the dependency, create a `Flow`:

```java
Flow flow = Flow.create(tracer);
```

If you need access to the current flow's id, call `currentId()` on it:

```java
entity.setLastModifiedBy(flow.currentId());
```

**Beware**: `Flow#currentId()` requires an [**active span**](https://opentracing.io/docs/overview/scopes-and-threading/) which is used
to keep track of the current flow id as part of the active span's [baggage](https://opentracing.io/docs/overview/tags-logs-baggage/#baggage-items).

The following table describes the contract how a flow id is propagated in different setups and scenarios:

| Trace-ID         | Upstream Baggage `flow_id` | Upstream `X-Flow-ID` Header | Downstream Baggage `flow_id` | Downstream `X-Flow-ID` Header |
|------------------|----------------------------|-----------------------------|------------------------------|-------------------------------|
| e28a8414294acf36 | n/a                        | n/a                         | n/a                          | e28a8414294acf36              |
| e28a8414294acf36 | REcCvlqMSReeo7adheiYFA     | n/a                         | REcCvlqMSReeo7adheiYFA       | REcCvlqMSReeo7adheiYFA        |
| e28a8414294acf36 | n/a                        | REcCvlqMSReeo7adheiYFA      | REcCvlqMSReeo7adheiYFA       | REcCvlqMSReeo7adheiYFA        |
| e28a8414294acf36 | REcCvlqMSReeo7adheiYFA     | Rso72qSgLWPNlYIF_OGjvA      | REcCvlqMSReeo7adheiYFA       | REcCvlqMSReeo7adheiYFA        |
| e28a8414294acf36 | n/a                        | e28a8414294acf36            | n/a                          | e28a8414294acf36              |
| e28a8414294acf36 | REcCvlqMSReeo7adheiYFA     | REcCvlqMSReeo7adheiYFA      | REcCvlqMSReeo7adheiYFA       | REcCvlqMSReeo7adheiYFA        |

## Servlet

On the server side is a single filter that you must be register in your filter chain. Make sure it runs very early — otherwise you might miss some crucial information when debugging.

You have to register the `FlowFilter` as a `Filter` in your filter chain:

```java
context.addFilter("FlowFilter", new FlowFilter(flow))
    .addMappingForUrlPatterns(EnumSet.of(REQUEST), true, "/*");
```

## Apache HTTP Client

Many client-side HTTP libraries on the JVM use the Apache HTTPClient, which is why `opentracing-flowid-httpclient` comes with a request interceptor:

```java
DefaultHttpClient client = new DefaultHttpClient();
client.addRequestInterceptor(new FlowHttpRequestInterceptor(flow));
```

### OkHttp

The `opentracing-flowid-okhttp` module contains an `Interceptor` to use with the `OkHttpClient`:

```java
OkHttpClient client = new OkHttpClient.Builder()
        .addNetworkInterceptor(new FlowInterceptor(flow))
        .build();
```

## Spring Boot Auto Configuration

*Tracer* comes with a convenient auto configuration for Spring Boot users that sets up aspect and servlet filter automatically with sensible defaults:

| Configuration                 | Description                               | Default                     |
|-------------------------------|-------------------------------------------|-----------------------------|
| `flowid.filter.enabled`       | Enables the [`FlowFilter`](#servlet)      | `true`                      |

```yaml
flowid:
    filter.enabled: true
```

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

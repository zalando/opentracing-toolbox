# Tracer

[![Highway at Night](docs/highway.jpg)](https://pixabay.com/en/highway-at-night-long-long-exposure-371009/)

[![Build Status](https://img.shields.io/travis/zalando/tracer.svg)](https://travis-ci.org/zalando/tracer)
[![Coverage Status](https://img.shields.io/coveralls/zalando/tracer.svg)](https://coveralls.io/r/zalando/tracer)
[![Release](https://img.shields.io/github/release/zalando/tracer.svg)](https://github.com/zalando/tracer/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/tracer-parent.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/tracer-parent)

*Tracer* manages custom trace identifiers and carries them through your system. This is usually a custom HTTP header the is created on the very first request and that gets added to any subsequent request and response, especially to transitive dependencies.

## Dependency

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-core</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

## Usage

You need to create a `Tracer` and specify the name of the traces you want to manage:

```java
Tracer tracer = Tracer.create("X-Trace-ID");
```

If you need access to the current trace's value you just call `getValue()` on it:

```java
Trace trace = tracer.get("X-Trace-ID"); // this is a live-view that can be a shared as a singleton
entity.setLastModifiedBy(trace.getValue());
```

### Generators

When starting a new trace *Tracer* by default will create a time-based UUID. This can be overridden on a per trace level:

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID", new CustomGenerator())
        .build();
```

For legacy reasons we use a different generator interally, the `FlowIDGenerator`. It basically renders a UUID as a base64-encoded byte array, e.g. `REcCvlqMSReeo7adheiYFA`.

### Listeners

For some use cases it might be useful to register a listener that gets notified everytime a trace is either started or stopped:

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID")
        .listener(new CustomTraceListener())
        .build();
```

## Logging

*Tracer* comes with a useful listener by default, the `MDCTraceListener`:

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID")
        .listener(new MDCTraceListener())
        .build();
```

It allows to prepend the trace id to every log line:

```xml
<PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} [%X{X-Trace-ID}] - %msg%n"/>
```

Another builtin listener is the `LoggingTraceListener` which logs the start and end of every trace.

## Apache HTTP Client

Many client-side HTTP libraries on the JVM use the Apache HTTPClient, which is why *Tracer* comes with a request interceptor:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-httpclient</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

```java
DefaultHttpClient client = new DefaultHttpClient();
client.addRequestInterceptor(new TracerHttpRequestInterceptor(tracer));
```

## Servlet

On the server side there is a single filter that needs to be registered in your filter chain. Make sure it runs very early, otherwise you might be missing some crucial information when debugging.

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-servlet</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

You have to register the `TracerFilter` as a `Filter` in your filter chain:

```java
context.addFilter("TracerFilter", new TracerFilter(tracer))
    .addMappingForUrlPatterns(EnumSet.of(REQUEST, ASYNC, ERROR), true, "/*"); 
```

## Aspect

For background jobs and tests you can either manage the lifecycle yourself:

```java
tracer.start();

try {
    // do work
} finally {
    tracer.stop();
}
```

or you can use the builtin aspect for it:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-aspectj</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

```java
@Traced
public void performBackgroundJob() {
    // do work
}
```

## Spring Boot Starter

Tracer comes with a convenient auto configuration for Spring Boot users:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-spring-boot-starter</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

It sets up all of the following parts automatically with sensible defaults:
- Aspect
- Servlet Filter
- MDC support

### Configuration

The following tables shows the available configuration. The only mandatory part is the actual traces, i.e. `tracer.traces`:

| Configuration             | Description                                                                       | Default                     |
|---------------------------|-----------------------------------------------------------------------------------|-----------------------------|
| `tracer.aspect.enabled`   | Enable the [`TracedAspect`](#aspect)                                              | `true`                      |
| `tracer.filter.enabled`   | Enable the [`TracerFilter`](#servlet)                                             | `true`                      |
| `tracer.logging.enabled`  | Enable the [`LoggingTraceListener`](#logging)                                     | `false`                     |
| `tracer.logging.category` | Changes the category of the [`LoggingTraceListener`](#logging)                    | `org.zalando.tracer.Tracer` |
| `tracer.mdc.enabled`      | Enable the [`MdcTraceListener`](#logging)                                         | `true`                      |
| `tracer.traces`           | Configure actual traces, mapping from name to generator type (`uuid` or`flow-id`) |                             |

```yaml
tracer:
    aspect.enabled: true
    filter.enabled: true
    logging:
        enabled: false
        category: org.zalando.tracer.Tracer
    mdc.enabled: true
    traces:
        X-Trace-ID: uuid
        X-Flow-ID: flow-id
```

The `TracerAutoConfiguration` will automatically pickup any `TraceListener` bound in the application context.

## License

Copyright [2015] Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

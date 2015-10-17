# Tracer

[![Highway at Night](https://github.com/zalando/tracer/raw/master/docs/highway.jpg)](https://pixabay.com/en/highway-at-night-long-long-exposure-371009/)

[![Build Status](https://img.shields.io/travis/zalando/tracer.svg)](https://travis-ci.org/zalando/tracer)
[![Coverage Status](https://img.shields.io/coveralls/zalando/tracer.svg)](https://coveralls.io/r/zalando/tracer)
[![Release](https://img.shields.io/github/release/zalando/tracer.svg)](https://github.com/zalando/tracer/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/tracer.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/tracer)

*Tracer* is a library that ... 

## Dependency

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>tracer-core</artifactId>
    <version>${tracer.version}</version>
</dependency>
```

## Usage

```java
Tracer tracer = Tracer.create("X-Trace-ID");
Trace trace = tracer.get("X-Trace-ID");

...

entity.setLastModifiedBy(trace.getValue());
```

### Generators

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID", new CustomGenerator())
        .build();
```

### Listeners

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID")
        .listener(new CustomTraceListener())
        .build();
```

## Logging

```java
Tracer tracer = Tracer.builder()
        .trace("X-Trace-ID")
        .listener(new MDCTraceListener())
        .build();
```

## Apache HTTP Client

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

### Background Job, Tests, ...

```java
tracer.start();

try {
    // do work
} finally {
    tracer.stop();
}
```

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

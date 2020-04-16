# OpenTracing: Proxy

[![Stability: Active](https://masterminds.github.io/stability/active.svg)](https://masterminds.github.io/stability/active.html)
![Build Status](https://github.com/zalando/opentracing-toolbox/workflows/Test/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/opentracing-toolbox/master.svg)](https://coveralls.io/r/zalando/opentracing-toolbox)
[![Code Quality](https://img.shields.io/codacy/grade/69e173024eec403797466e147a2051a3/master.svg)](https://www.codacy.com/app/whiskeysierra/opentracing-toolbox)
[![Javadoc](http://javadoc.io/badge/org.zalando/opentracing-proxy.svg)](http://www.javadoc.io/doc/org.zalando/opentracing-proxy)
[![Release](https://img.shields.io/github/release/zalando/opentracing-toolbox.svg)](https://github.com/zalando/opentracing-toolbox/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/opentracing-proxy.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/opentracing-proxy)
[![OpenTracing](https://img.shields.io/badge/OpenTracing-enabled-blue.svg)](http://opentracing.io)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/opentracing-toolbox/master/LICENSE)

*OpenTracing Proxy* is a library that adds the ability to observe and react to span activities in OpenTracing.

- **Technology stack**: Java 8+, OpenTracing
- **Status**: Under development and used in production

## Why not [opentracing-contrib/java-api-extensions](https://github.com/opentracing-contrib/java-api-extensions):question: 

- active spans are not proxied
- operation name not customizable
- scopes are not observable
- mutability
- stateful (each span has a copy of all tags)

## Example

```java
Tracer tracer = new ProxyTracer(original)
    .with(new Rename(CaseFormat.LOWER_UNDERSCORE))
    .with(new LogCorrelation()
        .withTraceId("trace_id")
        .withSpanId("span_id")
        .withBaggage("flow_id"))
    .with(new AutoTagging("flow_id"));
```

## Features

- [Customizable operation name](#naming)
- Observe [span/scope lifecycle events](#listeners)
  - start and finish
  - activate and close (scope)
  - new tags
  - new logs
  - new baggage
- [](#interceptors)
- Built-in, reusable, high-level [patterns](#patterns)
  - [auto-tagging](#auto-tagging)
  - [auto-baggage](#auto-baggage)
  - [log correlation](#log-correlation)
- Immutability
- Stateless

## Dependencies

- Java 8 or higher
- [OpenTracing](https://github.com/opentracing/opentracing-java)
- [SLF4J](https://www.slf4j.org/) (optional)

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>opentracing-proxy</artifactId>
    <version>${opentracing-proxy.version}</version>
</dependency>
```

## Configuration

```java
new ProxyTracer(tracer)
    .with(plugin)
    .with(anotherPlugin)
    .with(yetAnotherPlugin);
```

## Plugins

### Naming

Registering a `Naming` plugin allows to customize the operation name of all spans:

```java
new ProxyTracer(tracer)
    .with(naming(String::toLowerCase));
```

A slightly more sophisticated way to is provided by `Rename`:

```java
new Rename(CaseFormat.LOWER_HYPHEN)
```

It translates operations names into a specific [`CaseFormat`](https://guava.dev/releases/28.1-jre/api/docs/com/google/common/base/CaseFormat.html). The original case format is detected automatically. `Rename` allows to produce a consistent naming convention of operation names across different instrumentation libraries.

### Listeners

The majority of available `Plugin` types are listeners of various kinds. The following list shows all listeners and when they are being invoked:

- `BaggageListener`, new baggage item
- `LogListener`, new log fields
- `ScopeListener`, scope activate and close
- `SpanListener`, span start and finish
- `TagListener`, new tags

All listeners are reactive in the sense that they are being invoked after the fact. They can't influence the operation that was performed, but merely react to it. Listeners are generally useful in order to perform some additional task based on a relevant span activity. The most useful and common tasks can be found in the following section.

### Interceptors

Interceptors, compared to [listeners](#listeners) are not merely reacting to an action but can actively participate. Interceptors allow to replace or decorate certain aspects, e.g. `SpanBuilder` or `Span` instances and keep state in each decorator. This pattern is less frequently needed than listeners, but can be very powerful when needed.

## Patterns

### Log Correlation

Correlating log messages (referring to local log files, not OpenTracing span logs) is a very common use case. OpenTracing exposes the *Trace-Context identifiers* (Trace and Span ID) for this very [purpose](https://github.com/opentracing/specification/blob/master/rfc/trace_identifiers.md#log-correlation).

The built-in `LogCorrelation` plugin combines the reactive abilities of a `ScopeListener` and a `BaggageListener` with the [MDC](https://www.slf4j.org/manual.html#mdc) to create an out-of-the-box solution for log correlation. Every active `Span` will be correlated to the logs by pushing the relevant pieces to the mapped diagnostic context.

The following setup configures the plugin to expose the *Trace ID* as `trace_id`, the *Span ID* as `span_id` and the baggage item `flow_id` *as-is* to the MDC.

```java
Tracer tracer = new ProxyTracer(original)
    .with(new LogCorrelation()
        .withTraceId("trace_id")
        .withSpanId("span_id")
        .withBaggage("flow_id"));
```                          

It's also possible to map `request-id` baggage item to the `request_id` MDC: 

```java
Tracer tracer = new ProxyTracer(original)
    .with(new LogCorrelation()
        .withBaggage("request-id", "request_id"));
```                          

### Auto-Tagging

OpenTracing offers tags and baggage, both of which have very different characteristics. 

#### Tags
- Clients can't read them
- Shipped to and indexed by a central collector
- Not propagated to downstream dependencies

#### Baggage
- Client can read them
- Not necessarily shipped to or indexed by a central collector (vendor specific)
- Propagated to downstream dependencies

Due to the nature of those two constructs there are cases where one needs both of them. One example is Zalando's [*Flow ID*](https://opensource.zalando.com/restful-api-guidelines/#233) which we a) want to index and query by (tag) and b) need to propagate downstream (baggage).

One way to solve that is the `AutoTagging` plugin which automatically translates certain baggage items also into tags. Any instrumentation code would only need to deal with the baggage item and would get the tag automatically for free:

```java
Tracer tracer = new ProxyTracer(original)
    .with(new AutoTagging("flow_id"));
```

### Auto-Baggage

The `AutoBaggage` is pretty much the same as [*Auto-Tagging*](#auto-tagging) but in reverse, i.e. specific tags are automatically translated into baggage items.

:warning: Due to OpenTracing's API this plugin doesn't work when setting tags on a `SpanBuilder` since there is no way to add baggage items yet. It's therefore highly encouraged to only set tags on `Span` instances directly.

### Tag Propagation

OpenTracing does not propagate tags to child spans, unless they are explicitly set on the child span. Since tags can't be extracted from a span one would need to carry relevant tags out of band from parent span to child span by hand. That can be a cumbersome task if both locations are relative *far* away from each other, e.g. a servlet filter (parent span) and a client interceptor (child span).

The `TagPropagation` plugin eases this pain by automatically carrying over certain tags from parent to child spans:

```java
Tracer tracer = new ProxyTracer(original)
    .with(new TagPropagation("payment_method", "sales_channel", "country"));
```

Tag propagation in will **not** carry tags across application boundaries, i.e. they are not part of the span context and won't be transferred like baggage would.

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

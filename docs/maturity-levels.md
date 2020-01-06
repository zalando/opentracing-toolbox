# Maturity Levels

The quality of different OpenTracing instrumentation libraries that exist out there in the wild varies greatly. The following maturity levels can be used to classify different libraries and highlight their capabilities or shortcomings.

## 🌧️ Level 1: Take it or leave it

The most basic kind of instrumentation libraries allow for virtually **zero customization** whatsoever. As a user the only choice that is left up to you is whether to use the library or not. Characteristics include:

- Default tags/logs are pre-defined and hard-coded
- Disabling, replacing and/or extending tags/logs is not supported
- Default tags/logs are often used incorrectly in regards to the [semantic conventions](https://opentracing.io/specification/conventions/)
- Operation names are not customizable
- Span life cycle is not customizable
- Span activation (scope) is not customizable
- Span context extraction/injection is not customizable

### Examples

- [Apache CXF](http://cxf.apache.org/docs/using-opentracing.html)
- [OpenTracing AWS](https://github.com/opentracing-contrib/java-aws-sdk)
- [OpenTracing Hazelcast](https://github.com/opentracing-contrib/java-hazelcast)
- [OpenTracing JDBC (official)](https://github.com/opentracing-contrib/java-jdbc)  
  It does support to use the currently active span, rather than creating a new one though.
- [OpenTracing Spring Messaging](https://github.com/opentracing-contrib/java-spring-messaging)

## 🌦️ Level 2: Open-ish for extension

Instrumentation libraries at level 2, compared to level 1, have one aspect that is open for extension - adding tags/logs to spans. A very common and flexible pattern is the *span decorator* which, given a span and some library-specific context information, e.g. an HTTP request, decides which tag/log to add to the given span.  
With this ability a library is open for custom extensions from users, just by registering a custom span decorator. Characteristics include:

- **Span decorator pattern**
- Default tags/logs are grouped into a single decorator
- Default decorator is reusable
- Default tags/logs can't be used à la carte

### Examples

- [OpenTracing Apache Kafka](https://github.com/opentracing-contrib/java-kafka-client)
- [OpenTracing Elasticsearch](https://github.com/opentracing-contrib/java-elasticsearch-client)
- [OpenTracing gRPC](https://github.com/opentracing-contrib/java-grpc)  
  ⚠️ Default tags can't be changed, only extended
- [OpenTracing Java Web Servlet Filter](https://github.com/opentracing-contrib/java-web-servlet-filter)
- [OpenTracing JAX-RS](https://github.com/opentracing-contrib/java-jaxrs)
- [OpenTracing RabbitMQ](https://github.com/opentracing-contrib/java-rabbitmq-client)
- [OpenTracing Redis](https://github.com/opentracing-contrib/java-redis-client)
- [OpenTracing Spring RabbitMQ](https://github.com/opentracing-contrib/java-spring-rabbitmq)
- [OpenTracing Spring Web](https://github.com/opentracing-contrib/java-spring-web)

## 🌥️ Level 3: Tags and logs à la carte

Instrumentation libraries at level 3 are pretty much following the logical next step after level 2. Instead of having a single, default span decorator all meaningful tags/logs are spread across multiple, independent span decorators. Those span decorators can then be reused, excluded and combined freely. Characteristics include:

- Individual, reusable span decorators
- Composability of span decorators
- Customizable operation name (optional)

💡 Level-2 libraries can be promoted to Level 3 without the need to modify them directly. Their ability to accept at least one span decorator allows anyone to build and provide a reusable and composable set of individual span decorators.

## 🌤️ Level 4: Spans and scopes à la carte

Level-3 instrumentation libraries are all about customization of span tags and logs. At level 4 the extensibility expands into two further areas and libraries gain additional characteristics:

- **Customizable span lifecycle**  
  Useful to allow preparing spans or broadening their scope:
  - Create a new span
  - Use the active span
  - Pass a span explicitly
  - Cycle through those strategies in a given priority, e.g.
    1. Use explicitly passed span
    2. If no span was passed, use the active span
    3. If no span was active, create new span
- **Customizable span activation**  
  Useful because not every environment supports span activation (e.g. reactive systems):
  - Activate the span
  - Don't activate it

💡 Please note that each lifecycle can be freely combined with an activation strategy.

### Examples

- [OpenTracing JDBC (custom)](../opentracing-jdbc)  

## ☀️ Level 5: Span context à la carte

The 5th and final level allows to customize the last aspect of an instrumentation library - the [extraction and injection of the span context](https://opentracing.io/guides/java/inject-extract/) from and into a carrier respectively. Characteristics include:

- Enabled/disable span context extraction/injection

🚨 The ability to disable span context injection into e.g. outgoing requests is rarely needed and the default implementation or decision by most instrumentation libraries is useful out of the box. But it's always advisable to keep in mind that the span context is potentially exposing sensitive information (e.g. baggage items) or might even [break downstream systems if they are not expecting additional HTTP headers](https://github.com/opentracing-contrib/java-aws-sdk/pull/14).

### Examples

- [Riptide: OpenTracing](https://github.com/zalando/riptide/tree/master/riptide-opentracing)

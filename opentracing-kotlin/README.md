# Opentracing Kotlin
====================

This module has been designed to help Kotlin/Java developers reduce boiler plate code while intrumenting opentracing in
their services. It has many kotlin extension functions for the `io.opentracing.Tracer`
which will help with instrumentation.

Examples:

1) Let's look at the kotlin code below:

```kotlin
    tracer.trace(
    operation = "call_backendA",
    component = "retry",
    parent = parentSpan
) { span ->
    // do something
    span.setTag("response_status", 200)
    // do something more
    return something
}
```

The above code snippet is equivalent to the following code:

```kotlin
val builder = tracer.buildSpan(operation)
builder.apply {
    parentSpan?.let { asChildOf(parentSpan) }
    component?.let { withTag(Tags.COMPONENT, component) }
}
val span = builder.start()
var scope: Scope? = null
var closed = false

try {
    scope = scopeManager().activate(span)
    
    // do something
    span.setTag("response_status", 200)
    // do something more
    return something
} catch (e: Exception) {
    span.record(e)
    try {
        scope?.close()
        closed = true
    } catch (closeException: Exception) {
        // Do Nothing
    }
    throw e
} finally {
    try {
        if (!closed)
            scope?.close()
        span.finish()
    } catch (closeException: Exception) {
        // Do Nothing
    }
}
```

Using these extensions will help reduce a lot of boiler plate code. This can even be used from java code as shown below:

```java
TracerExtensionKT.trace(
    tracer,
    "call_backendA",
    "retry",
    null,
    parentSpan,
    (span) -> {
        // do something
        span.setTag("response_status",200)
        // do something more
        return something;
    });
```

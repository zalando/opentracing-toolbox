package org.zalando.opentracing.kotlin.suspend

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.tag.Tags
import org.zalando.opentracing.kotlin.record

suspend inline fun <R> Tracer.trace(
    span: Span,
    crossinline block: suspend (Span) -> R
): R {

    var scope: Scope? = null
    var closed = false

    try {
        scope = scopeManager().activate(span)

        return block(span)
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
}

suspend inline fun <R> Tracer.trace(
    operation: String,
    spanBuilder: Tracer.SpanBuilder.() -> Unit,
    crossinline block: suspend (Span) -> R
): R {
    val builder = buildSpan(operation)
    builder.apply(spanBuilder)
    val span = builder.start()
    return trace(span, block)
}

suspend inline fun <R> Tracer.trace(
    operation: String,
    component: String? = null,
    kind: String? = null,
    parent: SpanContext? = null,
    crossinline block: suspend (Span) -> R
): R {
    return trace(
        operation = operation,
        spanBuilder = {
            parent?.let { asChildOf(parent) }
            component?.let { withTag(Tags.COMPONENT, component) }
            kind?.let { withTag(Tags.SPAN_KIND, kind) }
        },
        block = block
    )
}

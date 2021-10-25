package org.zalando.opentracing.kotlin.suspend

import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.tag.Tags
import kotlinx.coroutines.CoroutineScope
import org.zalando.opentracing.kotlin.record
import kotlin.coroutines.coroutineContext

@SuppressWarnings("TooGenericExceptionCaught")
suspend inline fun <R> Tracer.trace(
    span: Span,
    crossinline block: suspend CoroutineScope.(Span) -> R
): R {

    try {
        return activeSpan(span) { this.block(span) }
    } catch (e: Exception) {
        span.record(e)
        throw e
    } finally {
        try {
            span.finish()
        } catch (closeException: Exception) {
            // Do Nothing
        }
    }
}

suspend inline fun <R> Tracer.trace(
    operation: String,
    spanBuilder: Tracer.SpanBuilder.() -> Unit,
    crossinline block: suspend CoroutineScope.(Span) -> R
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
    crossinline block: suspend CoroutineScope.(Span) -> R
): R {
    val parentSpan = parent ?: coroutineContext.activeSpan()?.context()
    return trace(
        operation = operation,
        spanBuilder = {
            parentSpan?.let { asChildOf(parentSpan) }
            component?.let { withTag(Tags.COMPONENT, component) }
            kind?.let { withTag(Tags.SPAN_KIND, kind) }
        },
        block = block
    )
}

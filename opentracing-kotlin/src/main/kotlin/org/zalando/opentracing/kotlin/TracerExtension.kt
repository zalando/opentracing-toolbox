package org.zalando.opentracing.kotlin

import io.opentracing.Scope
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.log.Fields
import io.opentracing.propagation.Format.Builtin.TEXT_MAP
import io.opentracing.propagation.TextMapAdapter
import io.opentracing.tag.Tags

inline fun <R> Tracer.trace(span: Span, block: (Span) -> R): R {

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

inline fun <R> Tracer.trace(
    operation: String,
    spanBuilder: Tracer.SpanBuilder.() -> Unit,
    block: (Span) -> R
): R {
    val builder = buildSpan(operation)
    builder.apply(spanBuilder)
    val span = builder.start()
    return trace(span, block)
}

inline fun <R> Tracer.trace(
    operation: String,
    component: String? = null,
    kind: String? = null,
    parent: SpanContext? = null,
    block: (Span) -> R
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

fun Span.record(ex: java.lang.Exception) {
    log(
        mapOf(
            Fields.EVENT to Tags.ERROR.key,
            Fields.ERROR_OBJECT to ex,
            Fields.MESSAGE to ex.message
        )
    )
    setTag(Tags.ERROR, true)
}

fun Tracer.injectToMap(span: Span? = activeSpan()): Map<String, String>? =
    span?.let {
        mutableMapOf<String, String>().apply {
            inject(
                it.context(),
                TEXT_MAP,
                TextMapAdapter(this)
            )
        }
    }

fun Tracer.extract(map: Map<String, String>?) = when {
    map == null -> null
    map.isEmpty() -> null
    else -> extract(TEXT_MAP, TextMapAdapter(map))
}

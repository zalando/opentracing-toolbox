package org.zalando.opentracing.kotlin.suspend

import io.opentracing.Span
import io.opentracing.Tracer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ActiveSpan(
    val span: Span
) :
    ThreadContextElement<Span>,
    AbstractCoroutineContextElement(ActiveSpan) {

    companion object Key : CoroutineContext.Key<ActiveSpan>

    override fun restoreThreadContext(context: CoroutineContext, oldState: Span) {

        span.log("switching from thread: ${Thread.currentThread().name}")
    }

    override fun updateThreadContext(context: CoroutineContext): Span =
        span.also { it.log("started in thread: ${Thread.currentThread().name}") }
}

suspend inline fun <R> activeSpan(
    span: Span,
    crossinline block: suspend CoroutineScope.(Span) -> R
) =
    withContext(coroutineContext + ActiveSpan(span)) {
        this.block(span)
    }

fun CoroutineContext.activeSpan() = get(ActiveSpan)?.span

suspend fun Tracer.currentSpan() = coroutineContext.activeSpan()

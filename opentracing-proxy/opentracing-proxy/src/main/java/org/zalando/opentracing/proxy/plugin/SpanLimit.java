package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.noop.NoopSpanBuilder;
import io.opentracing.noop.NoopSpanContext;
import io.opentracing.propagation.Format;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.intercept.injection.Injection;
import org.zalando.opentracing.proxy.intercept.span.SpanBuilderInterceptor;
import org.zalando.opentracing.proxy.listen.span.SpanListener;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

import static java.util.Collections.newSetFromMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class SpanLimit
        implements SpanBuilderInterceptor, SpanListener, Injection {

    /**
     * Holds {@link WeakReference weak references} to all non-finished
     * {@link Span spans}. It ensures that garbage-collected spans are counted
     * correctly.
     */
    private final Set<Span> spans = newSetFromMap(new WeakHashMap<>());

    private final int limit;

    @Override
    public SpanBuilder intercept(
            final Tracer tracer, final SpanBuilder builder) {

        if (spans.size() >= limit) {
            return NoopSpanBuilder.INSTANCE;
        }

        return builder;
    }

    @Override
    public void onStarted(final Span span) {
        spans.add(span);
    }

    @Override
    public void onFinished(final Span span) {
        spans.remove(span);
    }

    @Override
    public Injector intercept(
            final Injector injector,
            final SpanContext context,
            final Format<?> format) {

        // needed because we can't just pass any SpanContext to the
        // underlying tracer; most will fail
        // TODO verify that this works in conjunction with other plugins
        if (context instanceof NoopSpanContext) {
            return Injector.NOOP;
        }

        return injector;
    }

}

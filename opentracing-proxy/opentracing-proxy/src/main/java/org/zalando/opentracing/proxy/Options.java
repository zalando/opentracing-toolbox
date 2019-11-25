package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
final class Options implements Registry<Options> {

    private static final class Defaults {

        static final Naming ORIGINAL = name -> name;

        enum SpanBuilderInterceptors implements SpanBuilderInterceptor {
            NONE;

            @Override
            public SpanBuilder intercept(Tracer tracer, final SpanBuilder builder) {
                return builder;
            }
        }

        enum TagListeners implements TagListener {
            NONE
        }

        enum LogListeners implements LogListener {
            NONE;

            @Override
            public void onLog(final Span span, final Map<String, ?> fields) {
                // nothing to do
            }
        }

        enum BaggageListeners implements BaggageListener {
            NONE;

            @Override
            public void onBaggage(final Span span, final String key, final String value) {
                // nothing to do
            }
        }

        enum SpanListeners implements SpanListener {
            NONE
        }

        enum ScopeListeners implements ScopeListener {
            NONE
        }

    }

    private final Naming naming;
    private final SpanBuilderInterceptor spanBuilderInterceptor;
    private final TagListener tagListener;
    private final LogListener logListener;
    private final BaggageListener baggageListener;
    private final SpanListener spanListener;
    private final ScopeListener scopeListener;

    Options() {
        this(
                Defaults.ORIGINAL,
                Defaults.SpanBuilderInterceptors.NONE,
                Defaults.TagListeners.NONE,
                Defaults.LogListeners.NONE,
                Defaults.BaggageListeners.NONE,
                Defaults.SpanListeners.NONE,
                Defaults.ScopeListeners.NONE);
    }

    @Override
    public Options withNaming(final Naming naming) {
        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                logListener,
                baggageListener,
                spanListener,
                scopeListener);
    }

    @Override
    public Options withSpanBuilderInterceptor(
            final SpanBuilderInterceptor additionalSpanBuilderInterceptor) {

        return new Options(
                naming,
                SpanBuilderInterceptor.composite(
                        spanBuilderInterceptor,
                        additionalSpanBuilderInterceptor
                ),
                tagListener,
                logListener,
                baggageListener,
                spanListener,
                scopeListener);
    }

    @Override
    public Options withTagListener(final TagListener additionalTagListener) {
        return new Options(
                naming,
                spanBuilderInterceptor,
                TagListener.composite(
                        tagListener,
                        additionalTagListener),
                logListener,
                baggageListener,
                spanListener,
                scopeListener);
    }

    @Override
    public Options withLogListener(final LogListener additionalLogListener) {
        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                LogListener.composite(
                        logListener,
                        additionalLogListener),
                baggageListener,
                spanListener,
                scopeListener);
    }

    @Override
    public Options withBaggageListener(
            final BaggageListener additionalBaggageListener) {

        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                logListener,
                BaggageListener.composite(
                        baggageListener,
                        additionalBaggageListener),
                spanListener,
                scopeListener);
    }

    @Override
    public Options withSpanListener(
            final SpanListener additionalSpanListener) {

        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                logListener,
                baggageListener,
                SpanListener.composite(
                        spanListener,
                        additionalSpanListener),
                scopeListener);
    }

    @Override
    public Options withScopeListener(
            final ScopeListener additionalScopeListener) {

        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                logListener,
                baggageListener,
                spanListener,
                ScopeListener.composite(
                        scopeListener,
                        additionalScopeListener));
    }

    Naming naming() {
        return naming;
    }

    SpanBuilderInterceptor spanBuilders() {
        return spanBuilderInterceptor;
    }

    TagListener tags() {
        return tagListener;
    }

    LogListener logs() {
        return logListener;
    }

    BaggageListener baggage() {
        return baggageListener;
    }

    SpanListener spans() {
        return spanListener;
    }

    ScopeListener scopes() {
        return scopeListener;
    }

}

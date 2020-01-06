package org.zalando.opentracing.proxy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
final class Options implements Registry<Options> {

    private final Naming naming;
    private final SpanBuilderInterceptor spanBuilderInterceptor;
    private final TagListener tagListener;
    private final LogListener logListener;
    private final BaggageListener baggageListener;
    private final SpanListener spanListener;
    private final ScopeListener scopeListener;
    private final Injection injection;

    Options() {
        this(
                Naming.DEFAULT,
                SpanBuilderInterceptor.DEFAULT,
                TagListener.DEFAULT,
                LogListener.DEFAULT,
                BaggageListener.DEFAULT,
                SpanListener.DEFAULT,
                ScopeListener.DEFAULT,
                Injection.DEFAULT);
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
                scopeListener,
                injection);
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
                scopeListener,
                injection);
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
                scopeListener,
                injection);
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
                scopeListener,
                injection);
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
                scopeListener,
                injection);
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
                scopeListener,
                injection);
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
                        additionalScopeListener),
                injection);
    }

    public Options withInjection(
            final Injection additionalInjection) {

        return new Options(
                naming,
                spanBuilderInterceptor,
                tagListener,
                logListener,
                baggageListener,
                spanListener,
                scopeListener,
                Injection.composite(
                        injection,
                        additionalInjection
                ));
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

    Injection injections() {
        return injection;
    }

}

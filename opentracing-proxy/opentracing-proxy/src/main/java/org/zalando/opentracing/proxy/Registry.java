package org.zalando.opentracing.proxy;

public interface Registry<R extends Registry<R>> {

    R withNaming(Naming naming);

    R withSpanBuilderInterceptor(SpanBuilderInterceptor interceptor);

    R withTagListener(TagListener tagListener);
    R withLogListener(LogListener logListener);
    R withBaggageListener(BaggageListener baggageListener);
    R withSpanListener(SpanListener spanListener);
    R withScopeListener(ScopeListener scopeListener);

}

package org.zalando.opentracing.proxy;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface Registry<R extends Registry<R>> {

    R withNaming(Naming naming);
    R withSpanBuilderInterceptor(SpanBuilderInterceptor interceptor);
    R withTagListener(TagListener tagListener);
    R withLogListener(LogListener logListener);
    R withBaggageListener(BaggageListener baggageListener);
    R withSpanListener(SpanListener spanListener);
    R withScopeListener(ScopeListener scopeListener);
    R withInjection(Injection interceptor);

}

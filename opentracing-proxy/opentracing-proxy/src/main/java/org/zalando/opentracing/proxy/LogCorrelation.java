package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.slf4j.MDC;

import javax.annotation.Nullable;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class LogCorrelation implements ScopeListener, BaggageListener {

    @FunctionalInterface
    private interface Seed {
        @Nullable String valueOf(Span span);
    }

    private final ImMap<Seed, String> seeds;
    private final ImMap<String, String> baggage;

    public LogCorrelation() {
        this(PersistentHashMap.empty(), PersistentHashMap.empty());
    }

    public LogCorrelation withTraceId(final String contextKey) {
        final Seed seed = span -> span.context().toTraceId();
        return new LogCorrelation(seeds.assoc(seed, contextKey), baggage);
    }

    public LogCorrelation withSpanId(final String contextKey) {
        final Seed seed = span -> span.context().toSpanId();
        return new LogCorrelation(seeds.assoc(seed, contextKey), baggage);
    }

    public LogCorrelation withBaggage(final String key) {
        return withBaggage(key, key);
    }

    public LogCorrelation withBaggage(final String baggageKey, final String contextKey) {
        final Seed seed = span -> span.getBaggageItem(baggageKey);
        return new LogCorrelation(
                seeds.assoc(seed, contextKey),
                baggage.assoc(baggageKey, contextKey));
    }

    @Override
    public void onActivated(final Scope scope, final Span span) {
        seeds.forEach((seed, contextKey) ->
                Optional.ofNullable(seed.valueOf(span)).ifPresent(value ->
                        MDC.put(contextKey, value)));
    }

    @Override
    public void onBaggage(
            final Span span,
            final String baggageKey,
            final String value) {

        Optional.ofNullable(baggage.get(baggageKey))
                .ifPresent(contextKey -> MDC.put(contextKey, value));
    }

    @Override
    public void onClosing(final Scope scope, final Span span) {
        seeds.forEach((ignored, key) -> MDC.remove(key));
        baggage.forEach((ignored, key) -> MDC.remove(key));
    }

    @Override
    public <R extends Registry<R>> R registerTo(final R registry) {
        return registry
                .withScopeListener(this)
                .withBaggageListener(this);
    }

}

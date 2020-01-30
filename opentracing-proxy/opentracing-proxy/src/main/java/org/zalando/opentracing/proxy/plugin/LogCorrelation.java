package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.collections.PersistentHashMap;
import org.slf4j.MDC;
import org.zalando.opentracing.proxy.listen.baggage.BaggageListener;
import org.zalando.opentracing.proxy.listen.scope.ScopeListener;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class LogCorrelation implements ScopeListener, BaggageListener {

    @FunctionalInterface
    private interface Seed {
        @Nullable
        String valueOf(Span span);
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
        return withBaggage(singleton(key));
    }

    public LogCorrelation withBaggage(final Collection<String> keys) {
        return withBaggage(keys.stream()
                .collect(toMap(identity(), identity())));
    }

    public LogCorrelation withBaggage(
            final String baggageKey, final String contextKey) {
        return withBaggage(singletonMap(baggageKey, contextKey));
    }

    public LogCorrelation withBaggage(final Map<String, String> mapping) {
        return new LogCorrelation(
                reduce(mapping.entrySet(), seeds, (seeds, e) ->
                        seeds.assoc(baggageSeed(e.getKey()), e.getValue())),
                reduce(mapping.entrySet(), baggage, ImMap::assoc));
    }

    @Override
    public void onActivated(final Scope scope, final Span span) {
        seeds.forEach((seed, contextKey) ->
                Optional.ofNullable(seed.valueOf(span)).ifPresent(value ->
                        MDC.put(contextKey, value)));
    }

    @Override
    public void onBaggage(
            final Tracer tracer,
            final Span span,
            final String baggageKey,
            final String value) {

        @Nullable final Span activeSpan = tracer.activeSpan();

        if (span.equals(activeSpan)) {
            Optional.ofNullable(baggage.get(baggageKey))
                    .ifPresent(contextKey -> MDC.put(contextKey, value));
        }
    }

    @Override
    public void onClosing(final Scope scope, final Span span) {
        seeds.forEach((ignored, key) -> MDC.remove(key));
        baggage.forEach((ignored, key) -> MDC.remove(key));
    }

    private static Seed baggageSeed(final String key) {
        return span -> span.getBaggageItem(key);
    }

    private static <E, R> R reduce(
            final Iterable<E> elements,
            final R identity,
            final BiFunction<R, E, R> accumulator) {

        R result = identity;

        for (final E element : elements) {
            result = accumulator.apply(result, element);
        }

        return result;
    }

}

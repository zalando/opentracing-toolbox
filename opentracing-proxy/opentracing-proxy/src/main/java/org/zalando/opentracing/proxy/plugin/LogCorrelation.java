package org.zalando.opentracing.proxy.plugin;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.slf4j.MDC;
import org.zalando.opentracing.proxy.listen.baggage.BaggageListener;
import org.zalando.opentracing.proxy.listen.scope.ScopeListener;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.Collections.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class LogCorrelation implements ScopeListener, BaggageListener {
    private final ThreadLocal<Deque<Map<String, String>>> contexts = ThreadLocal.withInitial(LinkedList::new);

    @FunctionalInterface
    private interface Seed {
        @Nullable
        String valueOf(Span span);
    }

    private final ImmutableMap<Seed, String> seeds;
    private final ImmutableMap<String, String> baggage;

    public LogCorrelation() {
        this(ImmutableMap.of(), ImmutableMap.of());
    }

    public LogCorrelation withTraceId(final String contextKey) {
        final Seed seed = span -> span.context().toTraceId();
        return new LogCorrelation(assoc(seeds, seed, contextKey), baggage);
    }

    public LogCorrelation withSpanId(final String contextKey) {
        final Seed seed = span -> span.context().toSpanId();
        return new LogCorrelation(assoc(seeds, seed, contextKey), baggage);
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
                        assoc(seeds, baggageSeed(e.getKey()), e.getValue())),
                reduce(mapping.entrySet(), baggage, LogCorrelation::assoc));
    }

    @Override
    public void onActivated(final Scope scope, final Span span) {
        contexts.get().push(preserveContext());
        seeds.forEach((seed, contextKey) ->
                Optional.ofNullable(seed.valueOf(span))
                        .ifPresent(value -> MDC.put(contextKey, value)));
    }

    private Map<String, String> preserveContext() {
        final Map<String, String> context =
                newHashMapWithExpectedSize(seeds.size() + baggage.size());

        Stream.concat(seeds.values().stream(), baggage.values().stream())
                .forEach(contextKey -> {
                    @Nullable final String value = MDC.get(contextKey);
                    if (value == null) return;
                    context.put(contextKey, value);
                });

        return context;
    }

    @Override
    public void onBaggage(
            final Tracer tracer,
            final Span span,
            final String baggageKey,
            final String value) {

        final Function<Span, String> toSpanId = s -> s.context().toSpanId();

        @Nullable final String activeSpanId = Optional.ofNullable(tracer.activeSpan())
                .map(toSpanId)
                .orElse(null);

        if (toSpanId.apply(span).equals(activeSpanId)) {
            Optional.ofNullable(baggage.get(baggageKey))
                    .ifPresent(contextKey -> MDC.put(contextKey, value));
        }
    }

    @Override
    public void onClosing(final Scope scope, final Span span) {
        final Map<String, String> previousContext = restoreContext();
        contextKeys().forEach(key -> {
            @Nullable final String value = previousContext.get(key);
            if (value == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, value);
            }
        });
    }

    private static <K, V> ImmutableMap<K, V> assoc(
            final ImmutableMap<K, V> map, final Entry<K, V> entry) {

        return assoc(map, entry.getKey(), entry.getValue());
    }

    private static <K, V> ImmutableMap<K, V> assoc(
            final ImmutableMap<K, V> map, final K key, final V value) {

        return ImmutableMap.<K, V>builder()
                .putAll(map)
                .put(key, value)
                .build();
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

    private Map<String, String> restoreContext() {
        return Optional.ofNullable(contexts.get().poll()).orElse(emptyMap());
    }

    private Stream<String> contextKeys() {
        return Stream.concat(
                seeds.values().stream(),
                baggage.values().stream());
    }

}

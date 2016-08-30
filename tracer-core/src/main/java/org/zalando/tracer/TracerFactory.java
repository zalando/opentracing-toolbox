package org.zalando.tracer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Singular;

import static com.google.common.collect.Maps.toMap;
import static org.zalando.tracer.TraceListeners.compound;

public final class TracerFactory {

    TracerFactory() {
        // package private so we can trick code coverage
    }

    public static class Builder {

        public Builder stacked(final boolean stacked) {
            this.stacked = stacked;
            return this;
        }

        public Builder stacked() {
            this.stacked = true;
            return this;
        }

    }

    @lombok.Builder(builderClassName = "Builder")
    static Tracer create(
            final boolean stacked,
            @Singular final ImmutableList<String> traces,
            @Singular("trace") final ImmutableMap<String, Generator> customs,
            @Singular final ImmutableList<TraceListener> listeners) {

        final UUIDGenerator defaultGenerator = new UUIDGenerator();
        final ImmutableMap<String, Generator> combined = ImmutableMap.<String, Generator>builder()
                .putAll(customs)
                .putAll(toMap(traces, trace -> defaultGenerator))
                .build();

        if (stacked) {
            return new StackedTracer(combined, compound(listeners));
        } else {
            return new DefaultTracer(combined, compound(listeners));
        }
    }

}

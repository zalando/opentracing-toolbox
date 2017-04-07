package org.zalando.tracer;

import lombok.Singular;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

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

        public Builder append(final boolean append) {
            this.append = append;
            return this;
        }

        public Builder append() {
            this.append = true;
            return this;
        }

    }

    @lombok.Builder(builderClassName = "Builder")
    static Tracer create(
            final boolean stacked,
            final boolean append,
            @Singular final List<String> traces,
            @Singular("trace") final Map<String, Generator> customs,
            @Singular final List<TraceListener> listeners) {

        final UUIDGenerator defaultGenerator = new UUIDGenerator();
        final Map<String, Generator> combined = new LinkedHashMap<>();
        combined.putAll(customs);
        combined.putAll(traces.stream().collect(toMap(Function.identity(), trace -> defaultGenerator)));

        if (stacked && append) {
            return new StackedAppendTracer(combined, listeners);
        } else if (stacked) {
            return new StackedTracer(combined, listeners);
        } else {
            return new DefaultTracer(combined, listeners);
        }
    }

}

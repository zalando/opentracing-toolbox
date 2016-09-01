package org.zalando.tracer;

import java.util.Collection;

final class TraceListeners {

    TraceListeners() {
        // package private so we can trick code coverage
    }

    public static TraceListener compound(final Collection<TraceListener> listeners) {
        return new TraceListener() {
            @Override
            public void onStart(final String name, final String value) {
                listeners.forEach(listener -> listener.onStart(name, value));
            }

            @Override
            public void onStop(final String name, final String value) {
                listeners.forEach(listener -> listener.onStop(name, value));
            }
        };
    }

}

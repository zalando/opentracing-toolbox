package org.zalando.opentracing.proxy.listen.scope;

import io.opentracing.Scope;
import io.opentracing.Span;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface ScopeListener extends Plugin {

    ScopeListener DEFAULT = new ScopeListener() {
        // nothing to implement
    };

    default void onActivated(final Scope scope, final Span span) {
        // nothing to do
    }

    default void onClosing(final Scope scope, final Span span) {
        // nothing to do
    }

    default void onClosed(final Scope scope, final Span span) {
        // nothing to do
    }

    static ScopeListener composite(final ScopeListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static ScopeListener composite(final Iterable<ScopeListener> listeners) {
        return new CompositeScopeListener(listeners);
    }

}

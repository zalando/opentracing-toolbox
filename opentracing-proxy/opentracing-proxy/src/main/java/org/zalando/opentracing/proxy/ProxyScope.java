package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class ProxyScope implements Scope {

    private final Scope scope;
    private final Span span;
    private final Options options;

    @Override
    public void close() {
        try {
            options.scopes().onClosing(this, span);
        } finally {
            scope.close();
        }
        options.scopes().onClosed(this, span);
    }

}

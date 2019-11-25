package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

import javax.annotation.Nullable;

abstract class ForwardingScopeManager implements ScopeManager {

    protected abstract ScopeManager delegate();

    @Override
    public Scope activate(final Span span) {
        return delegate().activate(span);
    }

    @Nullable
    @Override
    public Span activeSpan() {
        return delegate().activeSpan();
    }

}

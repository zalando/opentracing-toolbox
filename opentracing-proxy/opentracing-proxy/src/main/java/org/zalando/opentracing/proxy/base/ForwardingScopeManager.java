package org.zalando.opentracing.proxy.base;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface ForwardingScopeManager extends ScopeManager {

    ScopeManager delegate();

    @Override
    default Scope activate(final Span span) {
        return delegate().activate(span);
    }

    @Nullable
    @Override
    default Span activeSpan() {
        return delegate().activeSpan();
    }


}

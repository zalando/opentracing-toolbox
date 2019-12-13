package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface Lifecycle {

    Optional<Span> start(Tracer tracer, String operationName);

    static Lifecycle composite(final Lifecycle... lifecyclePolicies) {
        return composite(Arrays.asList(lifecyclePolicies));
    }

    static Lifecycle composite(final Collection<Lifecycle> lifecyclePolicies) {
        return new CompositeLifecycle(lifecyclePolicies);
    }

}

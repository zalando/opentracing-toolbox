package org.zalando.opentracing.proxy;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface Plugin {
    <R extends Registry<R>> R registerTo(R registry);
}

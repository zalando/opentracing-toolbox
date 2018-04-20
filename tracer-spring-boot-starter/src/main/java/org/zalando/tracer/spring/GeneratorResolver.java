package org.zalando.tracer.spring;

import org.apiguardian.api.API;
import org.zalando.tracer.Generator;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public interface GeneratorResolver {

    Generator resolve(final String name);

}

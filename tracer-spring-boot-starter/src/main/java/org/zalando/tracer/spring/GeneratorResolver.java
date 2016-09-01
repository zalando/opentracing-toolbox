package org.zalando.tracer.spring;

import org.zalando.tracer.Generator;


public interface GeneratorResolver {

    Generator resolve(final String name);

}

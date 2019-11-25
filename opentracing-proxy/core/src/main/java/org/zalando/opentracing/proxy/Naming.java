package org.zalando.opentracing.proxy;

import org.apiguardian.api.API;

import java.util.function.UnaryOperator;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
@FunctionalInterface
public interface Naming extends Plugin {

    String rename(String operationName);

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withNaming(this);
    }

    static Naming naming(final UnaryOperator<String> operator) {
        return operator::apply;
    }

}

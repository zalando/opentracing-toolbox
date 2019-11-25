package org.zalando.opentracing.jdbc.operation;

import org.apiguardian.api.API;

import java.lang.reflect.Method;
import java.util.List;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class DefaultOperationName implements OperationName {

    @Override
    public String generate(final Method method, final List<String> queries) {
        return method.getName();
    }

}

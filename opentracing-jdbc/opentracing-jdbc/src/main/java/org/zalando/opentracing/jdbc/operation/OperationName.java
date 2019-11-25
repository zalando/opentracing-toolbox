package org.zalando.opentracing.jdbc.operation;

import org.apiguardian.api.API;

import java.lang.reflect.Method;
import java.util.List;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@FunctionalInterface
public interface OperationName {

    String generate(Method method, List<String> queries);

}

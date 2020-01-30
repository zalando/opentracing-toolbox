package org.zalando.opentracing.proxy.intercept.name;

import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.function.UnaryOperator;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface Naming extends Plugin {

    Naming DEFAULT = name -> name;

    @CheckReturnValue
    String rename(String operationName);

    static Naming naming(final UnaryOperator<String> operator) {
        return operator::apply;
    }

}

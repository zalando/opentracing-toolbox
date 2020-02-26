package org.zalando.opentracing.spring.web.extension;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.spring.web.extension.CompositeSpanDecorator.composite;

@API(status = EXPERIMENTAL)
public final class StandardSpanDecorator extends ForwardingSpanDecorator {

    public StandardSpanDecorator() {
        super(composite(
                new ErrorMessageSpanDecorator(),
                new ErrorSpanDecorator(),
                new ErrorStackSpanDecorator(),
                new HttpPathSpanDecorator(),
                HANDLER_METHOD_OPERATION_NAME,
                new ServiceLoaderSpanDecorator()
        ));
    }

}

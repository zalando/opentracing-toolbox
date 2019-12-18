package org.zalando.opentracing.jdbc.span;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.jdbc.span.SpanDecorator.composite;

@API(status = EXPERIMENTAL)
public final class StandardSpanDecorator extends ForwardingSpanDecorator {

    public StandardSpanDecorator() {
        super(composite(
                new ComponentSpanDecorator(),
                new DatabaseInstanceSpanDecorator(),
                new DatabaseStatementSpanDecorator(),
                new DatabaseTypeSpanDecorator(),
                new DatabaseUserSpanDecorator(),
                new ErrorMessageSpanDecorator(),
                new ErrorSpanDecorator(),
                new ErrorStackSpanDecorator(),
                new PeerSpanDecorator(),
                new ServiceLoaderSpanDecorator()
        ));
    }

}

package org.zalando.opentracing.servlet.extension;

import io.opentracing.contrib.web.servlet.filter.decorator.ServletFilterHeaderSpanDecorator;
import io.opentracing.contrib.web.servlet.filter.decorator.ServletFilterHeaderSpanDecorator.HeaderEntry;
import org.apiguardian.api.API;

import static java.util.Collections.singletonList;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpPreferSpanDecorator
        extends ForwardingSpanDecorator {

    public HttpPreferSpanDecorator() {
        super(new ServletFilterHeaderSpanDecorator(
                singletonList(new HeaderEntry("Prefer", "prefer")), "http."));
    }

}

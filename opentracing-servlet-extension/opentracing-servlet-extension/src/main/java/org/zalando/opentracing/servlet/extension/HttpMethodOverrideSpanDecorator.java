package org.zalando.opentracing.servlet.extension;

import io.opentracing.contrib.web.servlet.filter.decorator.ServletFilterHeaderSpanDecorator;
import io.opentracing.contrib.web.servlet.filter.decorator.ServletFilterHeaderSpanDecorator.HeaderEntry;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpMethodOverrideSpanDecorator
        extends ForwardingSpanDecorator {

    public HttpMethodOverrideSpanDecorator() {
        super(new ServletFilterHeaderSpanDecorator(Arrays.asList(
                new HeaderEntry("HTTP-Method-Override", "method_override"),
                new HeaderEntry("X-HTTP-Method-Override", "method_override")
        ), "http."));
    }

}

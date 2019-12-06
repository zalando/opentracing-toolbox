package org.zalando.opentracing.spring.webflux.extension;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.spring.webflux.extension.CompositeSpanDecorator.composite;

@API(status = EXPERIMENTAL)
public final class StandardSpanDecorator extends ForwardingSpanDecorator {

    public StandardSpanDecorator() {
        super(composite(
                new ComponentSpanDecorator(),
                new ErrorMessageSpanDecorator(),
                new ErrorSpanDecorator(),
                new ErrorStackSpanDecorator(),
                new HttpMethodOverrideSpanDecorator(),
                new HttpMethodSpanDecorator(),
                new HttpPathSpanDecorator(),
                new HttpPreferSpanDecorator(),
                new HttpStatusCodeSpanDecorator(),
                // HttpUrlSpanDecorator is intentionally not active by default
                new PeerSpanDecorator(),
                new HttpRetryAfterSpanDecorator(),
                new ServiceLoaderSpanDecorator()
        ));
    }

}

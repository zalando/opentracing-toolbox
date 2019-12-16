package org.zalando.opentracing.servlet.extension;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class StandardSpanDecorator extends ForwardingSpanDecorator {

    public StandardSpanDecorator() {
        super(CompositeSpanDecorator.composite(
                new ComponentSpanDecorator(),
                new ErrorMessageSpanDecorator(),
                new ErrorSpanDecorator(),
                new ErrorStackSpanDecorator(),
                new HttpMethodOverrideSpanDecorator(),
                new HttpMethodSpanDecorator(),
                new HttpPreferSpanDecorator(),
                new HttpRetryAfterSpanDecorator(),
                new HttpStatusCodeSpanDecorator(),
                // HttpUrlSpanDecorator is intentionally not active by default
                new PeerSpanDecorator(),
                new ServiceLoaderSpanDecorator()
        ));
    }

}

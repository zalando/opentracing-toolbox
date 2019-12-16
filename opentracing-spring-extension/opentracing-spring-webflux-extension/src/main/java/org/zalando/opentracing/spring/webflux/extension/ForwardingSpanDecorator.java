package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
abstract class ForwardingSpanDecorator
        implements WebFluxSpanDecorator {

    @Delegate
    private final WebFluxSpanDecorator delegate;

}

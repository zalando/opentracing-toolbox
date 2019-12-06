package org.zalando.opentracing.spring.web.extension;

import io.opentracing.contrib.spring.web.interceptor.HandlerInterceptorSpanDecorator;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
abstract class ForwardingSpanDecorator
        implements HandlerInterceptorSpanDecorator {

    @Delegate
    private final HandlerInterceptorSpanDecorator delegate;

}

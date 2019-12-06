package org.zalando.opentracing.spring.web.extension;

import io.opentracing.Span;
import io.opentracing.contrib.spring.web.interceptor.HandlerInterceptorSpanDecorator;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class CompositeSpanDecorator
        implements HandlerInterceptorSpanDecorator {

    private final Iterable<HandlerInterceptorSpanDecorator> decorators;

    @Override
    public void onPreHandle(
            final HttpServletRequest request,
            final Object handler,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onPreHandle(request, handler, span));
    }

    @Override
    public void onAfterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            @Nullable final Exception error,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onAfterCompletion(
                        request, response, handler, error, span));
    }

    @Override
    public void onAfterConcurrentHandlingStarted(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onAfterConcurrentHandlingStarted(
                        request, response, handler, span));
    }

    static HandlerInterceptorSpanDecorator composite(
            final HandlerInterceptorSpanDecorator... decorators) {
        return composite(Arrays.asList(decorators));
    }

    static HandlerInterceptorSpanDecorator composite(
            final Iterable<HandlerInterceptorSpanDecorator> decorators) {
        return new CompositeSpanDecorator(decorators);
    }

}

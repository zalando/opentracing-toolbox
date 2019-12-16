package org.zalando.opentracing.spring.web.extension;

import io.opentracing.Span;
import io.opentracing.contrib.spring.web.interceptor.HandlerInterceptorSpanDecorator;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

interface SpanDecorator extends HandlerInterceptorSpanDecorator {

    @Override
    default void onPreHandle(
            final HttpServletRequest request,
            final Object handler,
            final Span span) {
        // nothing to do
    }

    @Override
    default void onAfterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            @Nullable final Exception error,
            final Span span) {

        if (error == null) {
            onResponse(request, response, handler, span);
        } else {
            onError(request, response, handler, error, span);
        }
    }

    default void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Span span) {
        // nothing to do
    }

    default void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception error,
            final Span span) {
        // nothing to do
    }

    @Override
    default void onAfterConcurrentHandlingStarted(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Span span) {
        // nothing to do
    }

}

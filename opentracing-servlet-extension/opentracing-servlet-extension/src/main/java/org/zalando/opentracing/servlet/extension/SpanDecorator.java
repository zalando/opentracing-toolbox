package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

interface SpanDecorator extends ServletFilterSpanDecorator {

    @Override
    default void onRequest(
            final HttpServletRequest request,
            final Span span) {

        // nothing to do
    }

    @Override
    default void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Span span) {

        // nothing to do
    }

    @Override
    default void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Throwable error,
            final Span span) {

        // nothing to do
    }

    @Override
    default void onTimeout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long timeout,
            final Span span) {

        // nothing to do
    }

}

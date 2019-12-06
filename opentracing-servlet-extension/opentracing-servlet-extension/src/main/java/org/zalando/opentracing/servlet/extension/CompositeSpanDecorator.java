package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class CompositeSpanDecorator implements ServletFilterSpanDecorator {

    private final Iterable<ServletFilterSpanDecorator> decorators;

    @Override
    public void onRequest(
            final HttpServletRequest request,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onRequest(request, span));
    }

    @Override
    public void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Span span) {


        decorators.forEach(decorator ->
                decorator.onResponse(request, response, span));
    }

    @Override
    public void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Throwable throwable,
            final Span span) {


        decorators.forEach(decorator ->
                decorator.onError(request, response, throwable, span));
    }

    @Override
    public void onTimeout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long timeout,
            final Span span) {


        decorators.forEach(decorator ->
                decorator.onTimeout(request, response, timeout, span));
    }

    static ServletFilterSpanDecorator composite(
            final ServletFilterSpanDecorator... decorators) {
        return composite(Arrays.asList(decorators));
    }

    static ServletFilterSpanDecorator composite(
            final Iterable<ServletFilterSpanDecorator> decorators) {
        return new CompositeSpanDecorator(decorators);
    }

}

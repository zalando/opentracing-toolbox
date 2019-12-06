package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpMethodSpanDecorator implements SpanDecorator {

    @Override
    public void onRequest(final HttpServletRequest request, final Span span) {
        span.setTag(Tags.HTTP_METHOD, request.getMethod());
    }

}

package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpRetryAfterSpanDecorator implements SpanDecorator {

    private static final Tag<String> RETRY_AFTER = new StringTag("http.retry_after");

    @Override
    public void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Span span) {

        Optional.ofNullable(response.getHeader("Retry-After"))
                .ifPresent(retryAfter -> span.setTag(RETRY_AFTER, retryAfter));

    }

}

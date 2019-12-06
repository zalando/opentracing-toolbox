package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpUrlSpanDecorator implements SpanDecorator {

    @Override
    public void onRequest(final HttpServletRequest request, final Span span) {
        final StringBuffer url = request.getRequestURL();

        Optional.ofNullable(request.getQueryString())
                .ifPresent(query -> url.append("?").append(query));

        span.setTag(Tags.HTTP_URL, url.toString());
    }

}

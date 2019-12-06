package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>http.status_code</code> span tag.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class HttpStatusCodeSpanDecorator implements SpanDecorator {

    @Override
    public void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Span span) {

        span.setTag(Tags.HTTP_STATUS, response.getStatus());
    }

}

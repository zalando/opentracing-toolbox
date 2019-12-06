package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets arbitrary, static span tags.
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class StaticSpanDecorator implements SpanDecorator {

    private final Map<String, String> tags;

    @Override
    public void onRequest(final HttpServletRequest request, final Span span) {
        tags.forEach(span::setTag);
    }

}

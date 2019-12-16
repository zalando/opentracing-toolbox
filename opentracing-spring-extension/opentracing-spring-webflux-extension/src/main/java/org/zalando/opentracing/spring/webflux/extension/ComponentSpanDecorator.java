package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>component</code> span tag, defaults to <code>Servlet</code>.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class ComponentSpanDecorator implements SpanDecorator {

    private final String component;

    public ComponentSpanDecorator() {
        this("Spring WebFlux");
    }

    public ComponentSpanDecorator(final String component) {
        this.component = component;
    }

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        span.setTag(Tags.COMPONENT, component);
    }

}

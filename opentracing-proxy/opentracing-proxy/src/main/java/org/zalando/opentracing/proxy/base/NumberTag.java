package org.zalando.opentracing.proxy.base;

import io.opentracing.Span;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
final class NumberTag implements Tag<Number> {

    private final String key;

    @Override
    public void set(final Span span, final Number value) {
        span.setTag(key, value);
    }

}

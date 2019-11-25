package org.zalando.opentracing.proxy;

import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.BooleanTag;
import io.opentracing.tag.StringTag;

interface BaseSpanBuilder extends SpanBuilder {

    @Override
    default SpanBuilder withTag(final String key, final String value) {
        return withTag(new StringTag(key), value);
    }

    @Override
    default SpanBuilder withTag(final String key, final boolean value) {
        return withTag(new BooleanTag(key), value);
    }

    @Override
    default SpanBuilder withTag(final String key, final Number value) {
        return withTag(new NumberTag(key), value);
    }

}

package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.tag.BooleanTag;
import io.opentracing.tag.StringTag;

interface BaseSpan extends Span {

    @Override
    default Span setTag(final String key, final String value) {
        return setTag(new StringTag(key), value);
    }

    @Override
    default Span setTag(final String key, final boolean value) {
        return setTag(new BooleanTag(key), value);
    }

    @Override
    default Span setTag(final String key, final Number value) {
        return setTag(new NumberTag(key), value);
    }

}

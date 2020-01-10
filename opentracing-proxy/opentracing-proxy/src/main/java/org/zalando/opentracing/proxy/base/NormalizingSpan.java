package org.zalando.opentracing.proxy.base;

import io.opentracing.Span;
import io.opentracing.tag.BooleanTag;
import io.opentracing.tag.StringTag;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface NormalizingSpan extends Span {

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

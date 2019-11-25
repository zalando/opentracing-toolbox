package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;

public interface SimpleTagListener extends TagListener {

    default <T> void onTag(final Tag<T> tag, final T value) {
        // nothing to do
    }

    default <T> void onTag(
            final SpanBuilder builder,
            final Tag<T> tag,
            final T value) {

        onTag(tag, value);
    }

    default <T> void onTag(
            final Span span,
            final Tag<T> tag,
            final T value) {

        onTag(tag, value);
    }

}

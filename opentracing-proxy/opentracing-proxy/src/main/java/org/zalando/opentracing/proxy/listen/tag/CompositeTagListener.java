package org.zalando.opentracing.proxy.listen.tag;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeTagListener implements TagListener {

    private final Iterable<TagListener> listeners;

    @Override
    public <T> void onTag(
            final SpanBuilder builder,
            final Tag<T> tag,
            final T value) {

        listeners.forEach(listener ->
                listener.onTag(builder, tag, value));
    }

    @Override
    public <T> void onTag(final Span span, final Tag<T> tag, final T value) {
        listeners.forEach(listener ->
                listener.onTag(span, tag, value));
    }

}

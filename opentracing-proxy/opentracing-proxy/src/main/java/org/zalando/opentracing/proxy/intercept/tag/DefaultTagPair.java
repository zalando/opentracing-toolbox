package org.zalando.opentracing.proxy.intercept.tag;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.opentracing.proxy.intercept.tag.TagInterceptor.TagPair;
import org.zalando.opentracing.proxy.listen.tag.TagListener;

@AllArgsConstructor
@Getter
final class DefaultTagPair<T> implements TagPair {

    private final Tag<T> tag;
    private final T value;

    @Override
    public void tag(final SpanBuilder builder) {
        builder.withTag(tag, value);
    }

    @Override
    public void notify(final TagListener listener, final SpanBuilder builder) {
        listener.onTag(builder, tag, value);
    }

    @Override
    public void tag(final Span span) {
        span.setTag(tag, value);
    }

    @Override
    public void notify(final TagListener listener, final Span span) {
        listener.onTag(span, tag, value);
    }

}

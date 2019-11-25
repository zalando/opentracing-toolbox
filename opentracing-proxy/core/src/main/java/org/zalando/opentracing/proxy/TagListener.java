package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface TagListener extends Plugin {

    default <T> void onTag(
            final SpanBuilder builder,
            final Tag<T> tag,
            final T value) {

        // nothing to do
    }

    default <T> void onTag(
            final Span span,
            final Tag<T> tag,
            final T value) {

        // nothing to do
    }

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withTagListener(this);
    }

    static TagListener composite(final TagListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static TagListener composite(final Iterable<TagListener> listeners) {
        return new CompositeTagListener(listeners);
    }

}

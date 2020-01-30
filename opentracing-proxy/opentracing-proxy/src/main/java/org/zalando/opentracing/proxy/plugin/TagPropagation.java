package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.base.ForwardingSpan;
import org.zalando.opentracing.proxy.base.ForwardingSpanBuilder;
import org.zalando.opentracing.proxy.intercept.span.SpanBuilderInterceptor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.proxy.base.ForwardingSpan.unwrap;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class TagPropagation implements SpanBuilderInterceptor {

    private final Set<String> keys;

    public TagPropagation(final String... keys) {
        this(Arrays.asList(keys));
    }

    public TagPropagation(final List<String> keys) {
        this(new HashSet<>(keys));
    }

    @Override
    public SpanBuilder intercept(
            final Tracer tracer, final SpanBuilder builder) {
        return new PropagatingSpanBuilder(tracer, builder);
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private final class PropagatingSpanBuilder implements ForwardingSpanBuilder {

        private final Tracer tracer;
        private final SpanBuilder delegate;
        private final Map<Tag<?>, Object> tags = new HashMap<>();
        private boolean ignoreActiveSpan;

        @Override
        public SpanBuilder delegate() {
            return delegate;
        }

        @Override
        public <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
            with(tag, value);
            return ForwardingSpanBuilder.super.withTag(tag, value);
        }

        private <T> void with(final Tag<T> tag, final T value) {
            if (keys.contains(tag.getKey())) {
                tags.put(tag, value);
            }
        }

        @Override
        public SpanBuilder asChildOf(@Nullable final Span parent) {
            unwrap(parent, PropagatingSpan.class)
                    .ifPresent(span -> span.propagateTo(this));

            return ForwardingSpanBuilder.super.asChildOf(parent);
        }

        @Override
        public SpanBuilder ignoreActiveSpan() {
            this.ignoreActiveSpan = true;
            return ForwardingSpanBuilder.super.ignoreActiveSpan();
        }

        @Override
        public Span start() {
            final Span span = new PropagatingSpan(delegate.start());

            propagate(tags, span::setTag);

            if (ignoreActiveSpan) {
                return span;
            }

            @Nullable final Span active = tracer.activeSpan();

            unwrap(active, PropagatingSpan.class)
                    .ifPresent(parent -> parent.propagateTo(span));

            return span;
        }

    }

    @AllArgsConstructor(access = PRIVATE)
    private final class PropagatingSpan implements ForwardingSpan {

        private final Span delegate;
        private final Map<Tag<?>, Object> tags;

        PropagatingSpan(final Span delegate) {
            this(delegate, new HashMap<>());
        }

        @Override
        public Span delegate() {
            return delegate;
        }

        @Override
        public <T> Span setTag(final Tag<T> tag, final T value) {
            set(tag, value);
            return delegate.setTag(tag, value);
        }

        private <T> void set(final Tag<T> tag, final T value) {
            if (keys.contains(tag.getKey())) {
                tags.put(tag, value);
            }
        }

        void propagateTo(final SpanBuilder builder) {
            propagate(tags, builder::withTag);
        }

        void propagateTo(final Span span) {
            propagate(tags, span::setTag);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> void propagate(
            final Map<Tag<?>, Object> tags,
            final BiConsumer<Tag<T>, T> consumer) {

        tags.forEach((raw, value) -> {
            final Tag<T> tag = (Tag<T>) raw;
            consumer.accept(tag, (T) value);
        });
    }

}

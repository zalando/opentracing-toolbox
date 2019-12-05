package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.proxy.ProxySpan.unwrap;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class TagPropagation implements SpanBuilderInterceptor {

    private final Set<String> keys;

    public TagPropagation(final String... keys) {
        this(new HashSet<>(Arrays.asList(keys)));
    }

    @Override
    public SpanBuilder intercept(final Tracer tracer, final SpanBuilder builder) {
        return new PropagatingSpanBuilder(tracer, builder);
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private final class PropagatingSpanBuilder extends ForwardingSpanBuilder  {

        private final Tracer tracer;
        private final SpanBuilder delegate;
        private final Map<Tag<?>, Object> tags = new HashMap<>();
        private boolean ignoreActiveSpan;

        @Override
        protected SpanBuilder delegate() {
            return delegate;
        }

        @Override
        public <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
            with(tag, value);
            return super.withTag(tag, value);
        }

        private <T> void with(final Tag<T> tag, final T value) {
            if (keys.contains(tag.getKey())) {
                tags.put(tag, value);
            }
        }

        @Override
        public SpanBuilder asChildOf(@Nullable final Span parent) {
            ifPropagating(parent, span ->
                    span.propagateTo(this));

            return super.asChildOf(parent);
        }

        @Override
        public SpanBuilder ignoreActiveSpan() {
            this.ignoreActiveSpan = true;
            return super.ignoreActiveSpan();
        }

        @Override
        public Span start() {
            final Span span = new PropagatingSpan(super.start());

            propagate(tags, span::setTag);

            if (ignoreActiveSpan) {
                return span;
            }

            @Nullable final Span active = tracer.activeSpan();

            if (active == null) {
                return span;
            }

            // TODO the call to unwrap here prevents any meaningful composing
            //      of span builders
            ifPropagating(unwrap(active), parent ->
                    parent.propagateTo(span));

            return span;
        }

        private void ifPropagating(
                @Nullable final Span span,
                final Consumer<PropagatingSpan> action) {
            Optional.ofNullable(span)
                    .filter(PropagatingSpan.class::isInstance)
                    .map(PropagatingSpan.class::cast)
                    .ifPresent(action);
        }

    }

    @AllArgsConstructor
    private final class PropagatingSpan extends ForwardingSpan {

        private final Span delegate;
        private final Map<Tag<?>, Object> tags;

        PropagatingSpan(final Span delegate) {
            this(delegate, new HashMap<>());
        }

        @Override
        protected Span delegate() {
            return delegate;
        }

        @Override
        public <T> Span setTag(final Tag<T> tag, final T value) {
            set(tag, value);
            return super.setTag(tag, value);
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

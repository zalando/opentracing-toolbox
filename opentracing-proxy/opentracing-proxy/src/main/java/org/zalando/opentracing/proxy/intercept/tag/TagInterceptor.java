package org.zalando.opentracing.proxy.intercept.tag;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.listen.tag.TagListener;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Collections.singleton;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface TagInterceptor extends Plugin {

    TagInterceptor DEFAULT = new TagInterceptor() {
        // nothing to do
    };

    interface TagPair {

        void tag(SpanBuilder builder);
        void notify(TagListener listener, SpanBuilder builder);

        void tag(Span span);
        void notify(TagListener listener, Span span);

        static <T> TagPair of(final Tag<T> tag, final T value) {
            return new DefaultTagPair<>(tag, value);
        }

    }

    @CheckReturnValue
    default <T> Collection<TagPair> intercept(
            final Tag<T> tag, final T value) {

        return singleton(TagPair.of(tag, value));
    }

    static TagInterceptor composite(final TagInterceptor... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static TagInterceptor composite(final Collection<TagInterceptor> interceptors) {
        return new CompositeTagInterceptor(interceptors);
    }

}

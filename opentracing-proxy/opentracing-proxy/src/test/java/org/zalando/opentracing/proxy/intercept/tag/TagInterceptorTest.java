package org.zalando.opentracing.proxy.intercept.tag;

import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.IntTag;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;

class TagInterceptorTest {

    private final MockTracer tracer = new MockTracer();

    private final Tracer unit = new ProxyTracer(tracer)
            .with(new TagInterceptor() {
                @Override
                public <T> Collection<TagPair> intercept(final Tag<T> tag, final T value) {
                    return singleton(TagPair.of(new StringTag("type"), "test"));
                }
            })
            .with(new TagInterceptor() {
                @Override
                public <T> Collection<TagPair> intercept(final Tag<T> tag, final T value) {
                    return singletonList(TagPair.of(new IntTag("v"), 1));
                }
            });

    @Test
    void interceptsTags() {
        unit.buildSpan("test").start()
                .setTag("test", true)
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        final Map<String, Object> tags = span.tags();

        assertThat(tags, aMapWithSize(2));
        assertThat(tags, hasEntry("type", "test"));
        assertThat(tags, hasEntry("v", 1));
    }

}

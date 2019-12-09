package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

final class TagPropagationTest {

    private final MockTracer original = new MockTracer();
    private final Tracer tracer = new ProxyTracer(original)
            .with(new TagPropagation("special"));

    @Test
    void shouldPropagateSpecialTagsFromActiveParent() {
        final Span parent = tracer.buildSpan("parent")
                .withTag("special", true)
                .start();
        
        try (final Scope ignored = tracer.activateSpan(parent)) {
            tracer.buildSpan("child")
                    .start()
                    .finish();

            assertThat(tags(), hasEntry("special", true));
        }
    }

    @Test
    void shouldPropagateSpecialTagsFromExplicitParent() {
        final Span parent = tracer.buildSpan("parent")
                .withTag("special", 1.0)
                .start();

        tracer.buildSpan("child")
                .asChildOf(parent)
                .start()
                .finish();

        assertThat(tags(), hasEntry("special", 1.0));
    }

    @Test
    void shouldNotPropagateOrdinaryTagsToChild() {
        final Span parent = tracer.buildSpan("parent")
                .withTag("ordinary", "yes")
                .start();

        tracer.buildSpan("child")
                .asChildOf(parent)
                .start()
                .finish();

        assertThat(tags(), not(hasKey("ordinary")));
    }

    @Test
    void shouldPropagateSpecialTagsFromParentToChild() {
        final Span parent = tracer.buildSpan("parent")
                .start();
        
        parent.setTag("special", true);

        tracer.buildSpan("child")
                .asChildOf(parent)
                .start()
                .finish();

        assertThat(tags(), hasEntry("special", true));
    }

    @Test
    void shouldNotPropagateOrdinaryTagsFromParentToChild() {
        final Span parent = tracer.buildSpan("parent")
                .start();

        parent.setTag("ordinary", true);

        tracer.buildSpan("child")
                .asChildOf(parent)
                .start()
                .finish();

        assertThat(tags(), not(hasKey("ordinary")));
    }

    @Test
    void shouldPropagateSpecialTagsToGrandChild() {
        final Span grandParent = tracer.buildSpan("grand_parent")
                .withTag("special", true)
                .start();

        final Span parent = tracer.buildSpan("parent")
                .asChildOf(grandParent)
                .start();

        tracer.buildSpan("child")
                .asChildOf(parent)
                .start()
                .finish();

        assertThat(tags(), hasEntry("special", true));
    }

    @Test
    void shouldNotPropagateSpecialTagsIfActiveSpanIgnored() {
        final Span parent = tracer.buildSpan("parent")
                .withTag("special", true)
                .start();

        try (final Scope ignored = tracer.activateSpan(parent)) {
            tracer.buildSpan("child")
                    .ignoreActiveSpan()
                    .start()
                    .finish();

            assertThat(tags(), not(hasKey("special")));
        }
    }
    
    private Map<String, Object> tags() {
        return getOnlyElement(original.finishedSpans()).tags();
    }

}

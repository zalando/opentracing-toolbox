package org.zalando.opentracing.proxy.base;

import io.opentracing.Span;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockSpan.LogEntry;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class ForwardingSpanTest {

    private final MockTracer tracer = new MockTracer();
    private final MockSpan delegate = tracer.buildSpan("test").start();
    private final Span unit = (ForwardingSpan) () -> delegate;

    @Test
    void delegatesTag() {
        unit.setTag(Tags.ERROR, true);

        assertThat(delegate.tags(), hasEntry("error", true));
    }

    @Test
    void delegatesLogs() {
        unit.log(singletonMap("first", 1));
        unit.log(1337, singletonMap("first", 1));
        unit.log("test");
        unit.log(1337, "test");

        final List<LogEntry> entries = delegate.logEntries();

        assertThat(entries, hasSize(4));
        assertThat(entries.get(0).fields(), hasEntry("first", 1));
        assertThat(entries.get(1).fields(), hasEntry("first", 1));
        assertThat(entries.get(1).timestampMicros(), is(1337L));
        assertThat(entries.get(2).fields(), hasEntry("event", "test"));
        assertThat(entries.get(3).fields(), hasEntry("event", "test"));
        assertThat(entries.get(3).timestampMicros(), is(1337L));
    }

}

package org.zalando.opentracing.proxy.intercept.log;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockSpan.LogEntry;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

final class LogInterceptorTest {

    private final LogInterceptor interceptor = new LogInterceptor() {
        @Override
        public Collection<Log> intercept(
                final Span span, final Map<String, ?> fields) {
            return singleton(Log.of(singletonMap("v", 1)));
        }

        @Override
        public Collection<Log> intercept(
                final Span span, final long timestamp, final Map<String, ?> fields) {
            return singleton(Log.of(timestamp, singletonMap("v", 1)));
        }
    };

    private final MockTracer tracer = new MockTracer();

    private final Tracer unit = new ProxyTracer(tracer)
            .with(LogInterceptor.DEFAULT)
            .with(interceptor);

    @Test
    void interceptsLogs() {
        unit.buildSpan("test").start()
                .log("test")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        final List<LogEntry> entries = span.logEntries();

        assertThat(entries, hasSize(2));
        assertThat(entries.get(0).fields(), hasEntry("event", "test"));
        assertThat(entries.get(1).fields(), hasEntry("v", 1));
    }

    @Test
    void interceptsTimestampedLogs() {
        unit.buildSpan("test").start()
                .log(1337, "test")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        final List<LogEntry> entries = span.logEntries();

        assertThat(entries, hasSize(2));
        assertThat(entries.get(0).timestampMicros(), is(1337L));
        assertThat(entries.get(0).fields(), hasEntry("event", "test"));
        assertThat(entries.get(1).timestampMicros(), is(1337L));
        assertThat(entries.get(1).fields(), hasEntry("v", 1));
    }

}

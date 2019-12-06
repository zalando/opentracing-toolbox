package org.zalando.opentracing.servlet.extension;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class PeerSpanDecoratorTest {

    private final SpanDecorator unit = new PeerSpanDecorator();

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    private final MockTracer tracer = new MockTracer();
    private final MockSpan span = tracer.buildSpan("test").start();

    @Test
    void shouldTagHostnameIpV4() {
        when(request.getRemoteAddr()).thenReturn("192.100.0.1");
        when(request.getRemoteHost()).thenReturn("example");
        when(request.getRemotePort()).thenReturn(42000);

        unit.onRequest(request, span);

        assertThat(span.tags(), hasEntry("peer.address", "example:42000"));
        assertThat(span.tags(), hasEntry("peer.hostname", "example"));
        assertThat(span.tags(), hasEntry("peer.ipv4", "192.100.0.1"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));

        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

    @Test
    void shouldTagHostnameIpV6() {
        when(request.getRemoteAddr()).thenReturn("2001:db8::ff00:42:8329");
        when(request.getRemoteHost()).thenReturn("example");
        when(request.getRemotePort()).thenReturn(42000);

        unit.onRequest(request, span);

        assertThat(span.tags(), hasEntry("peer.address", "example:42000"));
        assertThat(span.tags(), hasEntry("peer.hostname", "example"));
        assertThat(span.tags(), hasEntry("peer.ipv6", "2001:db8::ff00:42:8329"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));

        assertThat(span.tags(), not(hasKey("peer.ipv4")));
    }

    @Test
    void shouldTagIpV4Only() {
        when(request.getRemoteAddr()).thenReturn("192.100.0.1");
        when(request.getRemoteHost()).thenReturn("192.100.0.1");
        when(request.getRemotePort()).thenReturn(42000);

        unit.onRequest(request, span);

        assertThat(span.tags(), hasEntry("peer.address", "192.100.0.1:42000"));
        assertThat(span.tags(), hasEntry("peer.ipv4", "192.100.0.1"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));

        assertThat(span.tags(), not(hasKey("peer.hostname")));
        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

    @Test
    void shouldTagIpV6Only() {
        when(request.getRemoteAddr()).thenReturn("2001:db8::ff00:42:8329");
        when(request.getRemoteHost()).thenReturn("2001:db8::ff00:42:8329");
        when(request.getRemotePort()).thenReturn(42000);

        unit.onRequest(request, span);

        assertThat(span.tags(), hasEntry("peer.address", "2001:db8::ff00:42:8329:42000"));
        assertThat(span.tags(), hasEntry("peer.ipv6", "2001:db8::ff00:42:8329"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));

        assertThat(span.tags(), not(hasKey("peer.hostname")));
        assertThat(span.tags(), not(hasKey("peer.ipv4")));
    }

}

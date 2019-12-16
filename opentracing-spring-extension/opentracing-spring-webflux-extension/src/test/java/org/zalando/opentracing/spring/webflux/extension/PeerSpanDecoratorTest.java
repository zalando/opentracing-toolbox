package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class PeerSpanDecoratorTest {

    private final SpanDecorator unit = new PeerSpanDecorator();

    private final ServerWebExchange exchange = mock(ServerWebExchange.class);
    private final ServerHttpRequest request = mock(ServerHttpRequest.class);

    private final MockTracer tracer = new MockTracer();
    private final MockSpan span = tracer.buildSpan("test").start();

    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    void shouldTagHostnameIpV4() throws UnknownHostException {
        final InetSocketAddress remoteAddress =
                new InetSocketAddress(
                        InetAddress.getByAddress(
                                "example",
                                new byte[] {(byte) 192, 100, 0, 1}),
                        42000);

        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        unit.onRequest(exchange, span);

        assertThat(span.tags(), hasEntry("peer.address", "example:42000"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));
        assertThat(span.tags(), hasEntry("peer.hostname", "example"));
        assertThat(span.tags(), hasEntry("peer.ipv4", "192.100.0.1"));

        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

    @Test
    void shouldTagHostnameIpV6() throws UnknownHostException {
        final InetSocketAddress remoteAddress =
                new InetSocketAddress(
                        InetAddress.getByAddress(
                                "example",
                                new byte[] {
                                        0b00100000,
                                        0b00000001,
                                        0b00001101,
                                        (byte) 0b10111000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        (byte) 0b11111111,
                                        0b00000000,
                                        0b00000000,
                                        0b01000010,
                                        (byte) 0b10000011,
                                        0b00101001
                                }),
                        42000);

        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        unit.onRequest(exchange, span);

        assertThat(span.tags(), hasEntry("peer.address", "example:42000"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));
        assertThat(span.tags(), hasEntry("peer.hostname", "example"));
        assertThat(span.tags(), hasEntry("peer.ipv6", "2001:db8:0:0:0:ff00:42:8329"));

        assertThat(span.tags(), not(hasKey("peer.ipv4")));
    }

    @Test
    void shouldTagIpV4Only() throws UnknownHostException {
        final InetSocketAddress remoteAddress =
                new InetSocketAddress(
                        InetAddress.getByAddress(
                                new byte[] {(byte) 192, 100, 0, 1}),
                        42000);

        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        unit.onRequest(exchange, span);

        assertThat(span.tags(), hasEntry("peer.address", "192.100.0.1:42000"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));
        assertThat(span.tags(), hasEntry("peer.ipv4", "192.100.0.1"));

        assertThat(span.tags(), not(hasKey("peer.hostname")));
        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

    @Test
    void shouldTagIpV6Only() throws UnknownHostException {
        final InetSocketAddress remoteAddress =
                new InetSocketAddress(
                        InetAddress.getByAddress(
                                new byte[] {
                                        0b00100000,
                                        0b00000001,
                                        0b00001101,
                                        (byte) 0b10111000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        0b00000000,
                                        (byte) 0b11111111,
                                        0b00000000,
                                        0b00000000,
                                        0b01000010,
                                        (byte) 0b10000011,
                                        0b00101001
                                }),
                        42000);

        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        unit.onRequest(exchange, span);

        assertThat(span.tags(), hasEntry("peer.address", "2001:db8:0:0:0:ff00:42:8329:42000"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));
        assertThat(span.tags(), hasEntry("peer.ipv6", "2001:db8:0:0:0:ff00:42:8329"));

        assertThat(span.tags(), not(hasKey("peer.hostname")));
        assertThat(span.tags(), not(hasKey("peer.ipv4")));
    }

    @Test
    void shouldNotTagIp() {
        final InetSocketAddress remoteAddress =
                InetSocketAddress.createUnresolved("localhost", 42000);

        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        unit.onRequest(exchange, span);

        assertThat(span.tags(), hasEntry("peer.address", "localhost:42000"));
        assertThat(span.tags(), hasEntry("peer.port", 42000));
        assertThat(span.tags(), hasEntry("peer.hostname", "localhost"));

        assertThat(span.tags(), not(hasKey("peer.ipv4")));
        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

    @Test
    void shouldNotTag() {
        unit.onRequest(exchange, span);

        assertThat(span.tags(), not(hasKey("peer.address")));
        assertThat(span.tags(), not(hasKey("peer.port")));
        assertThat(span.tags(), not(hasKey("peer.hostname")));
        assertThat(span.tags(), not(hasKey("peer.ipv4")));
        assertThat(span.tags(), not(hasKey("peer.ipv6")));
    }

}

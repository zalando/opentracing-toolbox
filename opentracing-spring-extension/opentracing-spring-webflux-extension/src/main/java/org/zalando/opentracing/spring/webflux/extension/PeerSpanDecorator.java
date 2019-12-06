package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Nullable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Optional;

import static io.opentracing.tag.Tags.PEER_HOSTNAME;
import static io.opentracing.tag.Tags.PEER_HOST_IPV6;
import static io.opentracing.tag.Tags.PEER_PORT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
public final class PeerSpanDecorator implements SpanDecorator {

    /**
     * Remote "address", suitable for use in a networking client library. This
     * may be a "ip:port", a bare "hostname", a FQDN, or even a JDBC substring
     * like "mysql://prod-db:3306".
     *
     * <strong>Be aware that this will be removed as soon as
     * {@link io.opentracing.tag.Tags} contains it.</strong>
     *
     * @see <a href="https://github.com/opentracing/opentracing-java/pull/358">opentracing/opentracing-java#358</a>
     */
    @API(status = EXPERIMENTAL)
    public static final Tag<String> PEER_ADDRESS = new StringTag("peer.address");

    /**
     * {@link io.opentracing.tag.Tags#PEER_HOST_IPV4} is typed as a
     * {@link Tag Tag<Integer>} for some unknown reason. This version of the
     * tag fixes that.
     *
     * @see io.opentracing.tag.Tags#PEER_HOST_IPV4
     */
    @API(status = INTERNAL)
    private static final Tag<String> PEER_HOST_IPV4 = new StringTag("peer.ipv4");

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        final ServerHttpRequest request = exchange.getRequest();

        Optional.ofNullable(request.getRemoteAddress()).ifPresent(address -> {
            final String hostname = address.getHostString();
            final int port = address.getPort();

            span.setTag(PEER_ADDRESS, hostname + ":" + port);
            span.setTag(PEER_PORT, port);

            @Nullable final InetAddress ip = address.getAddress();

            if (ip == null) {
                span.setTag(PEER_HOSTNAME, hostname);
            } else {
                if (!hostname.equals(ip.getHostAddress())) {
                    span.setTag(PEER_HOSTNAME, hostname);
                }

                tagIfMatch(span, Inet4Address.class, PEER_HOST_IPV4, ip);
                tagIfMatch(span, Inet6Address.class, PEER_HOST_IPV6, ip);
            }
        });
    }

    private void tagIfMatch(
            final Span span,
            final Class<? extends InetAddress> type, final Tag<String> tag,
            final InetAddress address) {

        if (type.isInstance(address)) {
            span.setTag(tag, address.getHostAddress());
        }
    }

}

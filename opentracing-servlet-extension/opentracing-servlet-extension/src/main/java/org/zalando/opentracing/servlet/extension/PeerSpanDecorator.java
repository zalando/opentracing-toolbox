package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;

import static io.opentracing.tag.Tags.PEER_HOSTNAME;
import static io.opentracing.tag.Tags.PEER_HOST_IPV6;
import static io.opentracing.tag.Tags.PEER_PORT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
public final class PeerSpanDecorator implements SpanDecorator {

    // TODO collect all tags in an ExtensionTags class somewhere, e.g. opentracing-toolbox-common?!

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
    private static final Tag<String> PEER_ADDRESS = new StringTag("peer.address");

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
    public void onRequest(final HttpServletRequest request, final Span span) {
        final String ip = request.getRemoteAddr();
        final String hostname = request.getRemoteHost();
        final int port = request.getRemotePort();

        span.setTag(PEER_ADDRESS, hostname + ":" + port);
        span.setTag(PEER_PORT, port);

        if (!ip.equals(hostname)) {
            span.setTag(PEER_HOSTNAME, hostname);
        }

        if (isProbablyIPv6(ip)) {
            span.setTag(PEER_HOST_IPV6, ip);
        } else {
            span.setTag(PEER_HOST_IPV4, ip);
        }
    }

    private boolean isProbablyIPv6(final String ip) {
        return ip.contains(":");
    }

}

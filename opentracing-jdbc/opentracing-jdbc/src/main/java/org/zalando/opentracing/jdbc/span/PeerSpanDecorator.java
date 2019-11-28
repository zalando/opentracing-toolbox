package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static io.opentracing.tag.Tags.PEER_HOSTNAME;
import static io.opentracing.tag.Tags.PEER_HOST_IPV6;
import static io.opentracing.tag.Tags.PEER_PORT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Sets the following span tags:
 *
 * <ul>
 *     <li><code>peer.hostname</code></li>
 *     <li><code>peer.port</code></li>
 *     <li><code>peer.ipv4</code></li>
 *     <li><code>peer.ipv6</code></li>
 * </ul>
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class PeerSpanDecorator implements SpanDecorator {

    private final PeerParser parser = PeerParser.create();

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
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) throws SQLException {

        final Connection connection = statement.getConnection();
        final Peer url = parser.parse(connection.getMetaData().getURL());

        tag(span, PEER_HOSTNAME, url.getHostname());
        tag(span, PEER_PORT, url.getPort());
        tag(span, PEER_HOST_IPV4, url.getIpv4());
        tag(span, PEER_HOST_IPV6, url.getIpv6());
    }

    private <T> void tag(final Span span, final Tag<T> tag, @Nullable final T value) {
        if (value != null) {
            span.setTag(tag, value);
        }
    }

}

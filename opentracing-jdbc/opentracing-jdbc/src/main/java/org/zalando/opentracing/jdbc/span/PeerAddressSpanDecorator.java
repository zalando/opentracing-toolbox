package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Sets the <code>peer.address</code> span tag.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class PeerAddressSpanDecorator implements SpanDecorator {

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

    @Override
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) throws SQLException {

        Optional.ofNullable(statement.getConnection().getMetaData().getURL())
                .map(this::trimPrefix)
                .ifPresent(url -> span.setTag(PEER_ADDRESS, url));
    }

    private String trimPrefix(final String url) {
        return trimPrefix(url, "jdbc:");
    }

    // visible for testing
    static String trimPrefix(final String s, final String prefix) {
        return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

}

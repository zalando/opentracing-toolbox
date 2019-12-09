package org.zalando.opentracing.jdbc.span;

import lombok.AllArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@AllArgsConstructor
final class CachingPeerParser implements PeerParser {

    private final ConcurrentMap<String, Peer> cache = new ConcurrentHashMap<>();

    private final PeerParser parser;

    @Override
    public Peer parse(final String url) {
        return cache.computeIfAbsent(url, parser::parse);
    }

}

package org.zalando.opentracing.jdbc.span;

interface PeerParser {

    Peer parse(String url);

    static PeerParser create() {
        return new CachingPeerParser(new PatternPeerParser());
    }

}

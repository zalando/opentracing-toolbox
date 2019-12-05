package org.zalando.opentracing.jdbc.span;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
final class Peer {

    static final Peer EMPTY = new Peer(null, null, null, null);

    @With
    private final String hostname;

    @With
    private final String ipv4;

    @With
    private final String ipv6;

    @With
    private final Integer port;

}

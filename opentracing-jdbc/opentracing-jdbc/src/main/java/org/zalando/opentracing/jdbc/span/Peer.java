package org.zalando.opentracing.jdbc.span;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.experimental.Wither;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
final class Peer {

    static final Peer EMPTY = new Peer(null, null, null, null);

    @Wither(onMethod = @__(@Generated))
    private final String hostname;

    @Wither(onMethod = @__(@Generated))
    private final String ipv4;

    @Wither(onMethod = @__(@Generated))
    private final String ipv6;

    @Wither(onMethod = @__(@Generated))
    private final Integer port;

}

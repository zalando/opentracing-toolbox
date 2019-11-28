package org.zalando.opentracing.jdbc.span;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachingPeerParserTest {

    private final PeerParser parser = mock(PeerParser.class);
    private final PeerParser unit = new CachingPeerParser(parser);

    @BeforeEach
    void setUp() {
        when(parser.parse(any())).thenReturn(Peer.EMPTY);
    }

    @Test
    void shouldDelegate() {
        final Peer peer = unit.parse("");
        assertEquals(Peer.EMPTY, peer);
    }

    @Test
    void shouldCache() {
        unit.parse("");
        unit.parse("");

        verify(parser, times(1)).parse("");
    }

}

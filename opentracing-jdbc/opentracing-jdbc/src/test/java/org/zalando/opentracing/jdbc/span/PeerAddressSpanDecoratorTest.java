package org.zalando.opentracing.jdbc.span;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.zalando.opentracing.jdbc.span.PeerAddressSpanDecorator.trimPrefix;

class PeerAddressSpanDecoratorTest {

    @Test
    void shouldTrimLeft() {
        assertEquals("World!", trimPrefix("Hello, World!", "Hello, "));
    }

    @Test
    void shouldNotTrimLeft() {
        assertEquals("Hello, World!", trimPrefix("Hello, World!", "Bye, "));
    }

}

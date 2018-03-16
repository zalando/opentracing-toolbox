package org.zalando.tracer.unit;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class TracerRuleBadCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @Test
    void shouldManageTrace() {
        final Trace trace = tracer.get("X-Trace-ID");
        assertThrows(IllegalStateException.class, trace::getValue);
    }

}

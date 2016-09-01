package org.zalando.tracer.unit;

import org.junit.Test;
import org.zalando.tracer.Tracer;

public final class TracerRuleBadCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @Test(expected = IllegalStateException.class)
    public void shouldManageTrace() {
        tracer.get("X-Trace-ID").getValue();
    }

}
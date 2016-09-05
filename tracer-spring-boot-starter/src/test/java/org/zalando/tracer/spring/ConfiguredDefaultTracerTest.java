package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.tracer.Tracer;

@ActiveProfiles("uuid")
public final class ConfiguredDefaultTracerTest extends AbstractTest {

    @Autowired
    private Tracer tracer;

    @Test(expected = IllegalStateException.class)
    public void shouldBeDefault() {
        tracer.start();
        tracer.get("X-Trace-ID");
        tracer.start();
    }

}

package org.zalando.tracer.spring;

import org.junit.Test;
import org.junit.Test.None;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.zalando.tracer.Tracer;

@ActiveProfiles("uuid")
@TestPropertySource(properties = {"tracer.stacked: true", "tracer.append: true"})
public final class ConfiguredStackedAppendTracerTest extends AbstractTest {

    @Autowired
    private Tracer tracer;

    @Test(expected = None.class)
    public void shouldBeStacked() {
        tracer.start();
        tracer.get("X-Trace-ID");
        tracer.start();
    }

}

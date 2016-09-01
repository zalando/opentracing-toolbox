package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;

@ActiveProfiles("uuid")
public final class UUIDGeneratorTest extends AbstractTest {

    @Autowired
    private Tracer tracer;

    @Test
    public void shouldConfigureFlowIdTrace() {
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();

        try {
            assertThat(trace, hasFeature("value", Trace::getValue,
                    hasFeature("length", String::length, is(36))));
        } finally {
            tracer.stop();
        }
    }

}

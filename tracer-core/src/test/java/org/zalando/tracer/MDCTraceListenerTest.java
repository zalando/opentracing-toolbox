package org.zalando.tracer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.MDC;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public final class MDCTraceListenerTest {

    @Rule
    public final TestRule mdc = new MDCRule();

    private final Tracer unit = Tracer.builder()
            .trace("X-Trace-ID", () -> "52aecbf6-73e1-11e5-b508-10ddb1ee7671")
            .listener(new MDCTraceListener())
            .build();

    @Test
    public void shouldNotBeSetUntilStarted() {
        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

    @Test
    public void shouldBeSetAfterStart() {
        unit.start();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }

    @Test
    public void shouldNotBeSetAfterStop() {
        unit.start();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

}
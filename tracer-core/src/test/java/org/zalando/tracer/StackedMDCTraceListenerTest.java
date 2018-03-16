package org.zalando.tracer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MDCExtension.class)
final class StackedMDCTraceListenerTest {

    private final Tracer unit = Tracer.builder()
            .trace("X-Trace-ID", () -> "52aecbf6-73e1-11e5-b508-10ddb1ee7671")
            .listener(new StackedMDCTraceListener())
            .stacked()
            .build();

    @Test
    void shouldNotBeSetUntilStarted() {
        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

    @Test
    void shouldBeSetAfterStart() {
        unit.start();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }

    @Test
    void shouldBeSetWithTwoAfterStartingStackedTrace() {
        unit.start();
        unit.start();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671 52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }

    @Test
    void shouldBeSetWithOneAfterStoppingInnerStackedTrace() {
        unit.start();
        unit.start();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }

    @Test
    void shouldNotBeSetAfterStop() {
        unit.start();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

    @Test
    void shouldNotBeSetAfterStopOuterStackedTrace() {
        unit.start();
        unit.start();
        unit.stop();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

}

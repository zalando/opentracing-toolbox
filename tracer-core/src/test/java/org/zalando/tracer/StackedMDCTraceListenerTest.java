package org.zalando.tracer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.MDC;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public final class StackedMDCTraceListenerTest {

    @Rule
    public final TestRule mdc = new MDCRule();

    private final Tracer unit = Tracer.builder()
            .trace("X-Trace-ID", () -> "52aecbf6-73e1-11e5-b508-10ddb1ee7671")
            .listener(new StackedMDCTraceListener())
            .stacked()
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
    public void shouldBeSetWithTwoAfterStartingStackedTrace() {
        unit.start();
        unit.start();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671 52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }


    @Test
    public void shouldBeSetWithTwoDifferentUuidsAfterStartingStackedAppendTrace() {
        final Tracer unit = Tracer.builder()
                .traces(asList("X-Trace-ID"))
                .listener(new StackedMDCTraceListener())
                .append()
                .stacked()
                .build();
        unit.start(tracerId -> "52aecbf6-73e1-11e5-b508-10ddb1ee7671");
        unit.start(tracerId -> "62aecbf6-73e1-11e5-b508-10ddb1ee7672");

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671 62aecbf6-73e1-11e5-b508-10ddb1ee7672"));
        unit.stop();
        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
        unit.stop();
        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

    @Test
    public void shouldBeSetWithTwoDifferentRandomUuidsAfterStartingStackedAppendTrace() {
        final Tracer unit = Tracer.builder()
                .traces(asList("X-Trace-ID"))
                .listener(new StackedMDCTraceListener())
                .append()
                .stacked()
                .build();
        unit.start();
        String randomUuid = MDC.get("X-Trace-ID");
        unit.start();
        assertThat(MDC.get("X-Trace-ID"), containsString(randomUuid));
        assertThat(MDC.get("X-Trace-ID"), is(not(randomUuid + " " + randomUuid)));
    }

    @Test
    public void shouldBeSetWithOneAfterStoppingInnerStackedTrace() {
        unit.start();
        unit.start();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is("52aecbf6-73e1-11e5-b508-10ddb1ee7671"));
    }

    @Test
    public void shouldNotBeSetAfterStop() {
        unit.start();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

    @Test
    public void shouldNotBeSetAfterStopOuterStackedTrace() {
        unit.start();
        unit.start();
        unit.stop();
        unit.stop();

        assertThat(MDC.get("X-Trace-ID"), is(nullValue()));
    }

}
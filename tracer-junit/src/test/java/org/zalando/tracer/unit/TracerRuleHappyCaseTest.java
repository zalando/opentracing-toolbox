package org.zalando.tracer.unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class TracerRuleHappyCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @Rule
    public final TestRule tracing = new TracerRule(tracer);

    @Test
    public void shouldManageTrace() {
        assertThat(tracer.get("X-Trace-ID").getValue(), is(notNullValue()));
    }

}
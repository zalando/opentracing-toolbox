package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.zalando.tracer.MDCTraceListener;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@TestPropertySource(properties = "tracer.mdc.enabled = false")
public final class DisabledMdcTest extends AbstractTest {

    @Autowired(required = false)
    private MDCTraceListener mdcTraceListener;

    @Test
    public void shouldDisableMdcTraceListener() {
        assertThat(mdcTraceListener, is(nullValue()));
    }

}

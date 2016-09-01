package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.tracer.MDCTraceListener;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class MdcTest extends AbstractTest {

    @Autowired
    private MDCTraceListener mdcTraceListener;

    @Test
    public void shouldEnableMdcTraceListenerByDefault() {
        assertThat(mdcTraceListener, is(notNullValue()));
    }

}

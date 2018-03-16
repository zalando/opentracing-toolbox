package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.MDCTraceListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = "tracer.mdc.enabled = false")
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class DisabledMDCTest {

    @Autowired(required = false)
    private MDCTraceListener mdcTraceListener;

    @Test
    void shouldDisableMdcTraceListener() {
        assertThat(mdcTraceListener, is(nullValue()));
    }

}

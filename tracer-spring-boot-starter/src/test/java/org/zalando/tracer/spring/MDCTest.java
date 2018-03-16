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
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class MDCTest {

    @Autowired
    private MDCTraceListener mdcTraceListener;

    @Test
    void shouldEnableMdcTraceListenerByDefault() {
        assertThat(mdcTraceListener, is(notNullValue()));
    }

}

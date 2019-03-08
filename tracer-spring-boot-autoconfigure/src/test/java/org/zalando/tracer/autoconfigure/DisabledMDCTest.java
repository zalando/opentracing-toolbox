package org.zalando.tracer.autoconfigure;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = "tracer.mdc.enabled = false")
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class DisabledMDCTest {

    @Autowired
    private Tracer tracer;

    @Test
    void shouldDisableMdc() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            assertThat(MDC.get("trace_id"), is(nullValue()));
            assertThat(MDC.get("span_id"), is(nullValue()));
        }
    }

}

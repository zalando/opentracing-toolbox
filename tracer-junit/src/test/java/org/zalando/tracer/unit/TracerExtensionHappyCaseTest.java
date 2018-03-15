package org.zalando.tracer.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zalando.tracer.Tracer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

final class TracerExtensionHappyCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @RegisterExtension
    final TracerExtension extension = new TracerExtension(tracer);

    @Test
    void shouldManageTrace() {
        assertThat(tracer.get("X-Trace-ID").getValue(), is(notNullValue()));
    }

}

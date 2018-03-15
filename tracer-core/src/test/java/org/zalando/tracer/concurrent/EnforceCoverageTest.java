package org.zalando.tracer.concurrent;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;

@Hack
@OhNoYouDidnt
final class EnforceCoverageTest {

    @Test
    void shouldUseTracingExecutorsContrstructor() {
        new TracingExecutors();
    }

}

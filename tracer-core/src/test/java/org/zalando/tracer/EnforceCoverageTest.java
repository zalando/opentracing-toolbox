package org.zalando.tracer;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Hack
@OhNoYouDidnt
final class EnforceCoverageTest {

    @Test
    void shouldUsePhraseGeneratorHolderConstructor() {
        new PhraseGenerator.Holder();
    }

    @Test
    void shouldUseTracerFactoryConstructor() {
        new TracerFactory();
    }

    @Test
    void shouldUseTraceListenersConstructor() {
        new TraceListeners();
    }


    @Test
    void shouldCoverTracerBuilderToString() {
        assertThat(Tracer.builder(), hasToString(notNullValue()));
    }

}

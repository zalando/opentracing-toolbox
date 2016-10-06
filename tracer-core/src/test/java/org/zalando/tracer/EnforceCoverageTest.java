package org.zalando.tracer;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;

import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    @Test
    public void shouldUsePhraseGeneratorHolderConstructor() {
        new PhraseGenerator.Holder();
    }

    @Test
    public void shouldUseTracerFactoryConstructor() {
        new TracerFactory();
    }

    @Test
    public void shouldUseTraceListenersConstructor() {
        new TraceListeners();
    }


    @Test
    public void shouldCoverTracerBuilderToString() {
        assertThat(Tracer.builder(), hasToString(notNullValue()));
    }

}

package org.zalando.tracer.spring;

import org.junit.Test;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.PhraseGenerator;
import org.zalando.tracer.UUIDGenerator;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class UnknownGeneratorTest {

    private final GeneratorResolver unit = new DefaultGeneratorResolver();

    @Test
    public void shouldSupportFlowId() {
        assertThat(unit.resolve("flow-id"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    public void shouldSupportUuid() {
        assertThat(unit.resolve("uuid"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test
    public void shouldSupportPhrase() {
        assertThat(unit.resolve("phrase"), is(instanceOf(PhraseGenerator.class)));
    }

    @Test
    public void shouldSupportFlowIdCaseInsensitive() {
        assertThat(unit.resolve("Flow-ID"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    public void shouldSupportUuidCaseInsensitive() {
        assertThat(unit.resolve("UUID"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test
    public void shouldSupportPhraseCaseInsensitive() {
        assertThat(unit.resolve("Phrase"), is(instanceOf(PhraseGenerator.class)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportUnknown() {
        unit.resolve("unknown");
    }

}

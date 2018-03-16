package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.PhraseGenerator;
import org.zalando.tracer.UUIDGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class UnknownGeneratorTest {

    private final GeneratorResolver unit = new DefaultGeneratorResolver();

    @Test
    void shouldSupportFlowId() {
        assertThat(unit.resolve("flow-id"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    void shouldSupportUuid() {
        assertThat(unit.resolve("uuid"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test
    void shouldSupportPhrase() {
        assertThat(unit.resolve("phrase"), is(instanceOf(PhraseGenerator.class)));
    }

    @Test
    void shouldSupportFlowIdCaseInsensitive() {
        assertThat(unit.resolve("Flow-ID"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    void shouldSupportUuidCaseInsensitive() {
        assertThat(unit.resolve("UUID"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test
    void shouldSupportPhraseCaseInsensitive() {
        assertThat(unit.resolve("Phrase"), is(instanceOf(PhraseGenerator.class)));
    }

    @Test
    void shouldNotSupportUnknown() {
        assertThrows(UnsupportedOperationException.class, () -> unit.resolve("unknown"));
    }

}

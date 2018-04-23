package org.zalando.tracer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {

    @Test
    void testUuid() {
        final String value = new UUIDGenerator().generate();

        assertThat(value, matchesPattern("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
    }

    @Test
    void testFlowId() {
        final String value = new FlowIDGenerator().generate();

        assertThat(value, matchesPattern("R[\\w-]{21}"));
    }

    @Test
    void testPhrase() {
        final String value = new PhraseGenerator().generate();

        assertThat(value, matchesPattern("[a-z]+_[a-z]+_[a-z]+_[a-z]+_[a-z]+"));
    }

    @Test
    void testPhraseWozniakIsNotBoring() {
        final Iterator<Integer> indexes = Arrays.asList(9, 143, 0, 0, 0, 0, 0, 0, 0, 0).iterator();

        final String value = PhraseGenerator.generate(i -> indexes.next());

        assertThat(value, is(not(containsString("boring_wozniak"))));
    }

    @Test
    void testPhraseOver_1_000_000_000() {
        assertTrue(PhraseGenerator.maxCombinations() > 1_000_000_000);
    }

    @Test
    void testPhraseMinLength() {
        assertThat(PhraseGenerator.minLength(), is(22));
    }

    @Test
    void testPhraseMaxLength() {
        assertThat(PhraseGenerator.maxLength(), is(61));
    }
}

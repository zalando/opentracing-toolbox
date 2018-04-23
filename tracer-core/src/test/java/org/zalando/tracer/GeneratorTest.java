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

        assertThat(value, matchesPattern("R[a-zA-Z0-9]{21}"));
    }

    @Test
    void testPhrase() {
        final String value = new PhraseGenerator().generate();

        assertThat(value, matchesPattern("[a-z]+_[a-z]+_[a-z]+_[a-z]+_[a-z]+"));
    }

    @Test
    void testRandom64() {
        final String value = new Random64Generator().generate();

        assertThat(value, matchesPattern("[a-f0-9]{16}"));
    }

    @Test
    void testRandom128() {
        final String value = new Random128Generator().generate();

        assertThat(value, matchesPattern("[a-f0-9]{32}"));
    }

    @Test
    void testPhraseWozniakIsNotBoring() {
        final Iterator<Integer> indexes = Arrays.asList(9, 143, 0, 0, 0, 0, 0, 0, 0, 0).iterator();

        final String value = new PhraseGenerator().generate(i -> indexes.next());

        assertThat(value, is(not(containsString("boring_wozniak"))));
    }

    @Test
    void testPhraseOver_1_000_000_000() {
        assertTrue(new PhraseGenerator().maxCombinations() > 1_000_000_000);
    }

    @Test
    void testPhraseMinLength() {
        assertThat(new PhraseGenerator().minLength(), is(22));
    }

    @Test
    void testPhraseMaxLength() {
        assertThat(new PhraseGenerator().maxLength(), is(61));
    }
}

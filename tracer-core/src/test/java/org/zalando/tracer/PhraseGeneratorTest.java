package org.zalando.tracer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhraseGeneratorTest {

    @Test
    void testPhraseWozniakIsNotBoring() {
        final Iterator<Integer> indexes = Arrays.asList(9, 143, 0, 0, 0, 0, 0, 0, 0, 0).iterator();

        final String value = new PhraseGenerator().generate(i -> indexes.next());

        assertThat(value, is(not(containsString("boring_wozniak"))));
    }

    @Test
    void shouldGenerateMoreThan1BillionCombinations() {
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

package org.zalando.tracer;

import java.util.Arrays;
import java.util.Iterator;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GeneratorTest {

    @Test
    public void testUuid() {
        final String value = new UUIDGenerator().generate();

        assertThat(value.length(), is(36));
    }

    @Test
    public void testFlowId() throws Exception {
        final String value = new FlowIDGenerator().generate();

        assertThat(value.length(), is(22));
    }

    @Test
    public void testPhrase() throws Exception {
        final String value = new PhraseGenerator().generate();

        assertThat(value, matchesPattern("[a-z]+_[a-z]+_[a-z]+_[a-z]+_[a-z]+"));
    }

    @Test
    public void testPhraseWozniakIsNotBoring() throws Exception {
        final Iterator<Integer> indexes = Arrays.asList(9, 143, 0, 0, 0, 0, 0, 0, 0, 0).iterator();

        final String value = PhraseGenerator.generate(i -> indexes.next());

        assertThat(value, is(not(containsString("boring_wozniak"))));
    }

    @Test
    public void testPhraseOver_1_000_000_000() throws Exception {
        assertTrue(PhraseGenerator.maxCombinations() > 1_000_000_000);
    }

    @Test
    public void testPhraseMinLength() throws Exception {
        assertThat(PhraseGenerator.minLength(), is(22));
    }

    @Test
    public void testPhraseMaxLength() throws Exception {
        assertThat(PhraseGenerator.maxLength(), is(61));
    }
}

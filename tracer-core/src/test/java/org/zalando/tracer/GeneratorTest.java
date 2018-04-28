package org.zalando.tracer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

class GeneratorTest {

    static Collection<Arguments> generators() {
        return Arrays.asList(
                Arguments.of(new FlowIDGenerator(), compile("R[\\w-]{21}")),
                Arguments.of(new PhraseGenerator(), compile("[a-z]+_[a-z]+_[a-z]+_[a-z]+_[a-z]+")),
                Arguments.of(new Random64Generator(), compile("[a-f0-9]{16}")),
                Arguments.of(new Random128Generator(), compile("[a-f0-9]{32}")),
                Arguments.of(new UUIDGenerator(), compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
        );
    }

    @ParameterizedTest
    @MethodSource("generators")
    void shouldMatchPattern(final Generator generator, final Pattern pattern) {
        assertThat(generator.generate(), matchesPattern(pattern));
    }

    @ParameterizedTest
    @MethodSource("generators")
    void shouldGenerateRandomValues(final Generator generator) {
        final String a = generator.generate();
        final String b = generator.generate();

        assertThat(a, is(not(equalTo(b))));
    }

}

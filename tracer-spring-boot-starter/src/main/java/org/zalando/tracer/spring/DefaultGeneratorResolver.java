package org.zalando.tracer.spring;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.zalando.tracer.PhraseGenerator;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;
import org.zalando.tracer.UUIDGenerator;

import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnMissingBean(GeneratorResolver.class)
class DefaultGeneratorResolver implements GeneratorResolver {

    private final ImmutableMap<String, Generator> generators = ImmutableMap.of(
            "flow-id", new FlowIDGenerator(),
            "uuid", new UUIDGenerator(),
            "phrase", new PhraseGenerator());

    @Override
    public Generator resolve(final String name) {
        return Optional.ofNullable(generators.get(name.toLowerCase(Locale.ROOT)))
                .orElseThrow(UnsupportedOperationException::new);
    }

}

package org.zalando.tracer.servlet;

import org.zalando.tracer.Generator;

// not final to support spying
class MockGenerator implements Generator {

    private final Generator generator;

    MockGenerator(final Generator generator) {
        this.generator = generator;
    }

    @Override
    public String generate() {
        return generator.generate();
    }

}

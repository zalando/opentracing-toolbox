package org.zalando.tracer;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Generates a random 128-bit hexadecimal integer.
 */
@API(status = EXPERIMENTAL)
public final class Random128Generator implements Generator {

    private final Random64Generator random64 = new Random64Generator();

    @Override
    public String generate() {
        return random64.generate() + random64.generate();
    }

}

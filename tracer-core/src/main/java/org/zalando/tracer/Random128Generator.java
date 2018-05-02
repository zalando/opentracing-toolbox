package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.concurrent.ThreadLocalRandom;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Generates a random 128-bit hexadecimal integer.
 */
@API(status = EXPERIMENTAL)
public final class Random128Generator implements Generator {

    @Override
    public String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        char[] chars = new char[32];

        Random64Generator.long2hex(chars, 0, random.nextLong());
        Random64Generator.long2hex(chars, 16, random.nextLong());

        return new String(chars);
    }
}

package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.concurrent.ThreadLocalRandom;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Generates a random 64-bit hexadecimal integer.
 */
@API(status = EXPERIMENTAL)
public final class Random64Generator implements Generator {

    @Override
    public String generate() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final char[] chars = new char[16];
        Chars.toLowerHex(random.nextLong(), chars, 0);
        return new String(chars);
    }
}

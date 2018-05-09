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
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final char[] chars = new char[32];
        Chars.toLowerHex(random.nextLong(), chars, 0);
        Chars.toLowerHex(random.nextLong(), chars, 16);
        return new String(chars);
    }
}

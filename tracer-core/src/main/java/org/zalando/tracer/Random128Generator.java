package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.concurrent.ThreadLocalRandom;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Generates a random 128-bit hexadecimal integer.
 */
@API(status = EXPERIMENTAL)
public final class Random128Generator implements Generator {

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    };

    @Override
    public String generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        char[] chars = new char[32];

        long2hex(chars, 0, random.nextLong());
        long2hex(chars, 16, random.nextLong());

        return new String(chars);
    }

    private void long2hex(char[] chars, int offset, long r) {
        chars[offset + 0] = DIGITS[(int) (r >> 4 * 0) & 0x0f];
        chars[offset + 1] = DIGITS[(int) (r >> 4 * 1) & 0x0f];
        chars[offset + 2] = DIGITS[(int) (r >> 4 * 2) & 0x0f];
        chars[offset + 3] = DIGITS[(int) (r >> 4 * 3) & 0x0f];
        chars[offset + 4] = DIGITS[(int) (r >> 4 * 4) & 0x0f];
        chars[offset + 5] = DIGITS[(int) (r >> 4 * 5) & 0x0f];
        chars[offset + 6] = DIGITS[(int) (r >> 4 * 6) & 0x0f];
        chars[offset + 7] = DIGITS[(int) (r >> 4 * 7) & 0x0f];
        chars[offset + 8] = DIGITS[(int) (r >> 4 * 8) & 0x0f];
        chars[offset + 9] = DIGITS[(int) (r >> 4 * 9) & 0x0f];
        chars[offset + 10] = DIGITS[(int) (r >> 4 * 10) & 0x0f];
        chars[offset + 11] = DIGITS[(int) (r >> 4 * 11) & 0x0f];
        chars[offset + 12] = DIGITS[(int) (r >> 4 * 12) & 0x0f];
        chars[offset + 13] = DIGITS[(int) (r >> 4 * 13) & 0x0f];
        chars[offset + 14] = DIGITS[(int) (r >> 4 * 14) & 0x0f];
        chars[offset + 15] = DIGITS[(int) (r >> 4 * 15) & 0x0f];
    }
}

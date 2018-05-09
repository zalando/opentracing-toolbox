package org.zalando.tracer;

final class Chars {

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    };

    private Chars() {

    }

    @SuppressWarnings("PointlessArithmeticExpression")
    static void toLowerHex(final long value, final char[] chars, final int offset) {
        chars[offset + 0] = DIGITS[(int) (value >> 4 * 0) & 0x0f];
        chars[offset + 1] = DIGITS[(int) (value >> 4 * 1) & 0x0f];
        chars[offset + 2] = DIGITS[(int) (value >> 4 * 2) & 0x0f];
        chars[offset + 3] = DIGITS[(int) (value >> 4 * 3) & 0x0f];
        chars[offset + 4] = DIGITS[(int) (value >> 4 * 4) & 0x0f];
        chars[offset + 5] = DIGITS[(int) (value >> 4 * 5) & 0x0f];
        chars[offset + 6] = DIGITS[(int) (value >> 4 * 6) & 0x0f];
        chars[offset + 7] = DIGITS[(int) (value >> 4 * 7) & 0x0f];
        chars[offset + 8] = DIGITS[(int) (value >> 4 * 8) & 0x0f];
        chars[offset + 9] = DIGITS[(int) (value >> 4 * 9) & 0x0f];
        chars[offset + 10] = DIGITS[(int) (value >> 4 * 10) & 0x0f];
        chars[offset + 11] = DIGITS[(int) (value >> 4 * 11) & 0x0f];
        chars[offset + 12] = DIGITS[(int) (value >> 4 * 12) & 0x0f];
        chars[offset + 13] = DIGITS[(int) (value >> 4 * 13) & 0x0f];
        chars[offset + 14] = DIGITS[(int) (value >> 4 * 14) & 0x0f];
        chars[offset + 15] = DIGITS[(int) (value >> 4 * 15) & 0x0f];
    }

}

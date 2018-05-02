package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FlowIDGenerator implements Generator {

    private static final int LENGTH = 128 / Byte.SIZE;

    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    @Override
    public String generate() {
        final byte[] bytes = new byte[LENGTH];
        ThreadLocalRandom.current().nextBytes(bytes);
        final byte[] encoded = encoder.encode(bytes);
        encoded[0] = 'R';
        return new String(encoded, US_ASCII);
    }

}

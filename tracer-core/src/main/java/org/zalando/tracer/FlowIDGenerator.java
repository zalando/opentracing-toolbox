package org.zalando.tracer;

import org.apiguardian.api.API;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FlowIDGenerator implements Generator {

    private final Base64.Encoder encoder = Base64.getUrlEncoder();

    @Override
    public String generate() {
        final Random random = ThreadLocalRandom.current();

        final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(random.nextLong());
        buffer.putLong(random.nextLong());

        final byte[] bytes = buffer.array();

        return "R" + encoder.encodeToString(bytes).replace("=", "").substring(1);
    }

}

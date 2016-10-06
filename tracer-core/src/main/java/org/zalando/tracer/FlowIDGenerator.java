package org.zalando.tracer;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public final class FlowIDGenerator implements Generator {

    private final Base64.Encoder encoder = Base64.getUrlEncoder();

    @Override
    public String generate() {
        final UUID uuid = UUID.randomUUID();

        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        final byte[] bytes = buffer.array();

        return "R" + encoder.encodeToString(bytes).replaceAll("=", "").substring(1);
    }

}

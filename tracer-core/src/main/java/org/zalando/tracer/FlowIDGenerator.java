package org.zalando.tracer;

import java.util.Base64;
import java.util.UUID;

import static com.google.common.primitives.Bytes.concat;
import static com.google.common.primitives.Longs.toByteArray;

public final class FlowIDGenerator implements Generator {

    private final Base64.Encoder encoder = Base64.getUrlEncoder();

    @Override
    public String generate() {
        final UUID uuid = UUID.randomUUID();
        final byte[] high = toByteArray(uuid.getMostSignificantBits());
        final byte[] low = toByteArray(uuid.getLeastSignificantBits());
        final byte[] bytes = concat(high, low);

        return "R" + encoder.encodeToString(bytes).replaceAll("=", "").substring(1);
    }

}

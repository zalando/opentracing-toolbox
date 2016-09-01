package org.zalando.tracer;

import java.util.UUID;

public final class UUIDGenerator implements Generator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}

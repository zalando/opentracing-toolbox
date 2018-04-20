package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.UUID;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class UUIDGenerator implements Generator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}

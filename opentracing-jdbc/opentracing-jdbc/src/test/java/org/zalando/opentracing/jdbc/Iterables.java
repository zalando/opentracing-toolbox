package org.zalando.opentracing.jdbc;

import java.util.Collection;

public final class Iterables {

    static <T> T getOnlyElement(final Collection<T> collection) {
        final int size = collection.size();

        if (size == 1) {
            return collection.iterator().next();
        }

        throw new IllegalArgumentException("Unexpected size: " + size);
    }

}

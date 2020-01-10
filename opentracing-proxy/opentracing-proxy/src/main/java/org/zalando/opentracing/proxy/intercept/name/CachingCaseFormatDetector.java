package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import static com.google.common.cache.CacheLoader.from;

final class CachingCaseFormatDetector implements CaseFormatDetector {

    private final LoadingCache<String, CaseFormat> cache;

    CachingCaseFormatDetector(final CaseFormatDetector detector) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1_000)
                .build(from(detector::detect));
    }

    @Override
    public CaseFormat detect(final String s) {
        return cache.getUnchecked(s);
    }

}

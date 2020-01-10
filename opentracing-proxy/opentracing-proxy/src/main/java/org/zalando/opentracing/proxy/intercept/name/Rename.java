package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class Rename implements Naming {

    private final CaseFormatDetector detector =
            new CachingCaseFormatDetector(new DefaultCaseFormatDetector());

    private final CaseFormat target;

    @Override
    public String rename(final String operationName) {
        final CaseFormat source = detector.detect(operationName);
        return source.to(target, operationName);
    }

}

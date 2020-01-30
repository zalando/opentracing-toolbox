package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultCaseFormatDetectorTest {

    private final CaseFormatDetector unit = new DefaultCaseFormatDetector();

    @ParameterizedTest
    @CsvSource({
            "detect_format, LOWER_UNDERSCORE",
            "DETECT_FORMAT, UPPER_UNDERSCORE",
            "detect-format, LOWER_HYPHEN",
            "detectFormat, LOWER_CAMEL",
            "DetectFormat, UPPER_CAMEL",
            "detect, LOWER_UNDERSCORE",
            "DETECT, UPPER_UNDERSCORE",
            "Detect, UPPER_CAMEL",
    })
    void detect(final String s, final CaseFormat format) {
        assertEquals(format, unit.detect(s));
    }
}

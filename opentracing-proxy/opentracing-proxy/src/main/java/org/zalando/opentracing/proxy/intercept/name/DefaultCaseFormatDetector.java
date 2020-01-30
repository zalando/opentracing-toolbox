package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;

final class DefaultCaseFormatDetector implements CaseFormatDetector {

    @Override
    public CaseFormat detect(final String s) {
        if (isLowerCase(s)) {
            if (hasHyphen(s)) {
                return CaseFormat.LOWER_HYPHEN;
            }

            return CaseFormat.LOWER_UNDERSCORE;
        }

        if (isUpperCase(s)) {
            return CaseFormat.UPPER_UNDERSCORE;
        }

        if (startsLowerCase(s)) {
            return CaseFormat.LOWER_CAMEL;
        }

        return CaseFormat.UPPER_CAMEL;
    }

    private boolean isLowerCase(final String s) {
        return s.equals(s.toLowerCase());
    }

    private boolean isUpperCase(final String s) {
        return s.equals(s.toUpperCase());
    }

    private boolean hasHyphen(final String s) {
        return s.contains("-");
    }

    private boolean startsLowerCase(final String s) {
        return Character.isLowerCase(s.charAt(0));
    }

}

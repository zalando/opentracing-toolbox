package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;

interface CaseFormatDetector {
    CaseFormat detect(String s);
}

package org.zalando.opentracing.proxy;

import com.google.common.base.CaseFormat;

interface CaseFormatDetector {
    CaseFormat detect(String s);
}

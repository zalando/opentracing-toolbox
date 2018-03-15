package org.zalando.tracer.aspectj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.tracer.Trace;

@Service
class TracedService {

    @Autowired
    private Trace trace;

    @Traced
    String withAspect() {
        return trace.getValue();
    }

    @Traced
    String failWithAspect() {
        throw new UnsupportedOperationException();
    }

    String withoutAspect() {
        return trace.getValue();
    }

}

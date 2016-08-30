package org.zalando.tracer.aspectj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.tracer.Trace;

@Service
public class TracedService {

    @Autowired
    private Trace trace;

    @Traced
    public String withAspect() {
        return trace.getValue();
    }

    public String withoutAspect() {
        return trace.getValue();
    }

}

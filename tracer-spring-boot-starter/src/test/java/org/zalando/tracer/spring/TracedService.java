package org.zalando.tracer.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.tracer.Trace;
import org.zalando.tracer.aspectj.Traced;

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

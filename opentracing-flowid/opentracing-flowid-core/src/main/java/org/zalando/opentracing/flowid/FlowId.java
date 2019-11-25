package org.zalando.opentracing.flowid;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface FlowId {

    enum Source {
        BAGGAGE, HEADER, TRACE
    }

    String getValue();
    Source getSource();

}

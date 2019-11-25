package org.zalando.opentracing.flowid;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
final class SimpleFlowId implements FlowId {
    private final String value;
    private final Source source;
}

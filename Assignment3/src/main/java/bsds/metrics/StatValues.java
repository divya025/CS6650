package bsds.metrics;

import lombok.Value;

@Value
public class StatValues {
    private long timestamp;
    private MyMetricsEnum myMetricsEnum;
    private long value;
}

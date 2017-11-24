package bsds.metrics;

// ENUM TO STORE THE METRICS

/*
* Since I am using single post to post each record individualy instead of using batches
*  ENUM works well to record the mertrics recieved.
*  If using batches ( with chache ) I would have choosen to use a queue to avoid metrics
*  bottlenecks.
 */

public enum MyMetricsEnum {
    RECORD_ERRORS,
    RECORD_LATENCY,
    RECORD_POST_ERROR,
    RECORD_POST_LATENCY,
    METRICS_ERRORS,
    METRICS_LATENCY,
    METRICS_GET_ERROR,
    METRICS_GET_LATENCY
}

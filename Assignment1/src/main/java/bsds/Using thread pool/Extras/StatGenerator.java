package bsds.Usingthreadpool.Extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatGenerator {

  private List<Result> results;
  private int numSuccesses;
  private int numRequests;
  private int totalLatency;

  public StatGenerator(List<Result> results) {
    this.results = results;
    for (Result result : results) {
      numSuccesses += result.getSuccessCount();
      numRequests += result.getRequestCount();
      totalLatency += result.totalLatency();
    }
  }

  // Get number of successful requests
  public int getNumSuccesses() {
    return numSuccesses;
  }

  // Get total number of requests (failed + successful)
  public int getNumRequests() {
    return numRequests;
  }

  // Get 99 and 95th percentiles from latency
  public int latencyPercentile(int percentile) {
    int index = (percentile * numSuccesses / 100).intValue() - 1;
    List<Integer> latencies = getLatencies();
    Collections.sort(latencies);
    return latencies.get(index);
  }

  // Get median from latency = 50th percentile
  public int median() {
    return latencyPercentile(50);
  }

  // Get the mean from latency = total/n
  public int mean() {
    return totalLatency/numSuccesses;
  }

  // Extracts and combines all latency times from each Result object
  private List<Integer> getLatencies() {
    List<Integer> allTimes = new ArrayList<>();
    for(Result result : results)
      allTimes.addAll(result.getLatencies());
    return allTimes;
  }
}

package bsds.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Statistics for the Program whichis threadsafe
 */
public class Stat {
    private int successRequestsNum;
    private int sentRequestsNum;
    private long totalLatency;
    private boolean isSorted;
    private List<Long> latencies;
    private Map<Integer, List<Integer>> timeToLatencies;
    
    public Stat() {
        this.successRequestsNum = 0;
        this.sentRequestsNum = 0;
        this.totalLatency =0;
        this.isSorted = false;
        this.latencies = new ArrayList<>();
        this.timeToLatencies = new HashMap<>();
    }

    public Stat getInstance(){
        return new Stat();
    }

    public void recordLatency(long latency) {
        totalLatency += latency;
        latencies.add(latency);
    }

    public void recordSentRequestNum() {
        sentRequestsNum += 1;
    }

    public void recordSuccessfulRequestNum(boolean isSuccess) {
        successRequestsNum += isSuccess ? 1 : 0;
    }

    public synchronized void addLatencyMapping(long timel, long latencyl) {
        int time = Math.toIntExact(timel);
        int latency = Math.toIntExact(latencyl);
        if(timeToLatencies.containsKey(time)) {
            List<Integer> currentList = timeToLatencies.get(time);
            currentList.add(latency);
            timeToLatencies.put(time, currentList);
        }
        else {
            List<Integer> newlist = new ArrayList<>();
            newlist.add(latency);
            timeToLatencies.put(time, newlist);
        }
    }

    public long getMeanLatency() {
        return totalLatency / successRequestsNum;
    }

    public long getMedianLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get(latencies.size() / 2);
    }

    public long get95thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get((int)Math.floor(latencies.size() * 0.95));
    }

    public long get99thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get((int)Math.floor(latencies.size() * 0.99));
    }

    public int getSuccessRequestsNum() {
        return successRequestsNum;
    }

    public int getSentRequestsNum() {
        return sentRequestsNum;
    }

    public long getTotalLatency() {
        return totalLatency;
    }
    
    public synchronized List<Long> getLatencies() {
        return latencies;
    }
    
    public Map<Integer, List<Integer>> getLatencyMap() {
        return timeToLatencies;
    }
    

    
}

package bsds.client;


import bsds.model.RecordData;
import bsds.util.StatUtil;
import bsds.util.OutputGraph;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;


/*
* @author divyaagarwal
*
 */

public class MyClientGET {


    // ALL DATA MEMBERS

    // Number of threads for each of the server, Client, DB
    private static int taskSize = 100;
    private static int dbThreads = 32;
    private static int serverThreads = 50;
    // Set the dayNum for loading the records
    private static int dayNum = 4;

    // PATHS for each of the days
    private static String PATH999 ="/Users/divyaagarwal/Desktop/bsds/Day999.csv";
    private static String PATH1 ="/Users/divyaagarwal/Desktop/bsds/Day1.csv";
    private static String PATH2 ="/Users/divyaagarwal/Desktop/bsds/Day2.csv";
    private static String PATH3="/Users/divyaagarwal/Desktop/bsds/Day3.csv";
    private static String PATH4 ="/Users/divyaagarwal/Desktop/bsds/Day4.csv";

    //private static String URL = "http://localhost:8080";
    private static String URL = "http://finallb1-1861644484.us-west-2.elb.amazonaws.com:8080";

    private final WebTarget webTarget;
    private final Executor executor;
    private final Map<Long, Long> latencyMap;

    public MyClientGET(WebTarget webTarget, Executor executor, Map<Long, Long> latencyMap) {
        this.webTarget = webTarget;
        this.executor = executor;
        this.latencyMap = latencyMap;
    }


    public Response get() throws ClientErrorException {
        long startTime = System.currentTimeMillis();
        Response response = webTarget
                .request()
                .get(Response.class);
        long endTime = System.currentTimeMillis();
        executor.execute(() -> latencyMap.put(endTime, endTime - startTime));
        return response;
    }


    private Function<String, RecordData> mapToItem = (line) -> {
        String[] entries = line.split(",");
        RecordData item = new RecordData(Integer.parseInt(entries[0]),
                Integer.parseInt(entries[1]),
                Integer.parseInt(entries[2]),
                Integer.parseInt(entries[3]),
                Integer.parseInt(entries[4]));
        return item;
    };


    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();


        // Use multi threading to post data
        ExecutorService executorService = Executors.newFixedThreadPool(taskSize);

        // Use the single thread ofr collecting stats data
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        // Store the stats / Metrics
        final Map<Long, Long> statMap = new ConcurrentHashMap<>(1 << 18);

        System.out.println("!!!!!  Reading Skier MyMetricsEnum !!!!!!");

        final long startTime = System.currentTimeMillis();
        System.out.println("Read Start time: " + new Date(startTime));
        for (int i = 0; i < taskSize; i++) {
            final int factor = i;
            executorService.submit(() -> {
                for (int j = 1; j <= 400; j++) {
                    int skierId = 400 * factor + j;
                    WebTarget webTarget = client.target(URL+"/Assignment2_war/myvert?dayNum=1&skierId=" + skierId);

                    MyClientGET myRunnable = new MyClientGET(webTarget, singleThreadExecutor, statMap);
                    myRunnable.get();
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.MINUTES);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            System.out.println("Read End time: " + new Date(endTime));
            System.out.println("All read threads complete (time taken):" + timeTaken);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        singleThreadExecutor.shutdown();
        try {
            singleThreadExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Collection<Long> latencyValues = statMap.values();

        System.out.println("    !!! END OF REQUEST !!!");
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("     STATISTICS");
        System.out.println("----------------------------------");
        System.out.println("1. Number of threads: " + taskSize);

        System.out.println("2. Median latency: " + StatUtil.findMedian(latencyValues) + " ms");
        System.out.println("3. Mean latency: " + StatUtil.findMean(latencyValues) + " ms");
        System.out.println("4. 95th percentile :" + StatUtil.findPercentile(latencyValues, 0.95) + " ms");
        System.out.println("5. 99th percentile :" + StatUtil.findPercentile(latencyValues, 0.99) + " ms");

        OutputGraph.plotGraph("Read graph", statMap, startTime, "read");
    } //end of main
} // end of class




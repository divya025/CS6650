package bsds.client;


import bsds.model.RecordData;
import bsds.util.StatUtil;
import bsds.util.OutputGraph;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/*
* @author divyaagarwal
*
 */

public class MyClientPOST {

    // ALL DATA MEMBERS

    // Number of threads for each of the server, Client, DB
    private static int taskSize = 120;
    private static int dbThreads = 32;
    private static int serverThreads = 50;
    // Set the dayNum for loading the records
    private static int dayNum = 1;

    // PATHS for each of the days
    private static String PATH999 = "/Users/divyaagarwal/Desktop/bsds/Day999.csv";
    private static String PATH1 = "/Users/divyaagarwal/Desktop/bsds/Day1.csv";
    private static String PATH2 = "/Users/divyaagarwal/Desktop/bsds/Day2.csv";
    private static String PATH3 = "/Users/divyaagarwal/Desktop/bsds/Day3.csv";
    private static String PATH4 = "/Users/divyaagarwal/Desktop/bsds/Day4.csv";

    //private static String URL = "http://localhost:8080/rest/load";
    private static String URL = "http://finallb1-1861644484.us-west-2.elb.amazonaws.com:8080/Assignment2_war/rest/load";


    private final WebTarget webTarget;
    private final Executor executor;
    private final Map<Long, Long> latencyMap;

    public MyClientPOST(WebTarget webTarget, Executor executor, Map<Long, Long> latencyMap) {
        this.webTarget = webTarget;
        this.executor = executor;
        this.latencyMap = latencyMap;
    }

    public Response post(Object requestEntity) throws ClientErrorException {
        long startTime = System.currentTimeMillis();
        Response response = webTarget
                .request()
                .post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), Response.class);
        long endTime = System.currentTimeMillis();
        executor.execute(() -> latencyMap.put(endTime, endTime - startTime));
        return response;
    }

    // Split the data into fileds
    private Function<String, RecordData> rfidLiftDataFunction = (line) -> {
        String[] entries = line.split(",");
        RecordData item = new RecordData(Integer.parseInt(entries[0]),
                Integer.parseInt(entries[1]),
                Integer.parseInt(entries[2]),
                Integer.parseInt(entries[3]),
                Integer.parseInt(entries[4]));
        return item;
    };

    private List<RecordData> readCSVFile(InputStream inputStream) {
        // read data from csv file
        List<RecordData> recordData;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // skip the first line of the csv
            recordData = br.lines().skip(1).map(rfidLiftDataFunction).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return recordData;
    }


    private InputStream makeStream(String path) {
        // Convert the data into a stream
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return in;
    }

    public static void main(String[] args) {


        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URL);

        // use multi threading to post data
        ExecutorService executorService = Executors.newFixedThreadPool(taskSize);

        // Use the single thread ofr collecting stats data
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

        // Store the stats / Metrics
        Map<Long, Long> statMap = new ConcurrentHashMap<>();

        // Post the data you read as streams
        MyClientPOST postData;
        postData = new MyClientPOST(webTarget, singleThreadExecutor, statMap);


        // Find the current path based on DayNumber


        List<RecordData> dataStream = postData.readCSVFile(postData.makeStream(PATH4));

        System.out.println("Number of Records to be written: " + dataStream.size());
        System.out.println("!!!!!  Writing Skier data !!!!!!");
        long startTime = System.currentTimeMillis();
        System.out.println("Write Start time: " + new Date(startTime));
        for (RecordData liftData : dataStream) {
            executorService.submit(() -> postData.post(liftData.toJson()));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.MINUTES);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            System.out.println("Write End time: " + new Date(endTime));
            System.out.println("All write threads complete (time taken):" + timeTaken);
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

        OutputGraph.plotGraph("Write graph", statMap, startTime, "write");
    }
}




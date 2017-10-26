package bsds.client;

import bsds.model.Record;
import bsds.utils.OutputChart;
import bsds.utils.Stat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.util.Date;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

// This is the client for the program (Entry Point)
public class MyClient {
    private final String URL = "http://localhost:7070";
    //private final String URL = "http://34.214.91.35:8080/Assignment2-tomcat_war/rest/load";
    static final String FILE_PATH = "/Users/divyaagarwal/Desktop/bsds/Assignment2/BSDSAssignment2Day1.csv";

    static final String FILE_PATH2 = "/Users/divyaagarwal/Desktop/bsds/Assignment2/BSDSAssignment2Day2.csv";

    private List<Record> Records= new ArrayList<Record>();

    // Read the .csv file to get the data
    public void readCsvFile(String path) {

        // make sure the ArrayList is empty before you start recording the data
        if (!Records.isEmpty()) {
            Records.clear();
        }
        try {
            // read data from the .csv file
            System.out.println("Reading array list....");
            BufferedReader br = new BufferedReader(new FileReader(path));
            // Skip the first line
            String line = br.readLine();
            while ((line = br.readLine()) != null) {

                String[] fields = line.split(",");
                Record record = new Record( // the fields in .csv are jumbled
                        Integer.parseInt(fields[2]), // skierID
                        Integer.parseInt(fields[3]), // liftID
                        Integer.parseInt(fields[1]), // dayNum
                        Integer.parseInt(fields[4])); // time
                Records.add(record);
            }

            br.close();
            System.out.println("!!Reading completed!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postTasks(int numThreads) {

        //final String URL = "http://34.214.91.35:8080/Assignment2-tomcat_war/rest/load";
        final String URL = "http://localhost:7070/rest/load";
        System.out.println("Starting POST requests...");
        long startTime = System.currentTimeMillis();

        // Testing for 10000 data
        // Test for entire dataset use : Records.size()
        System.out.println("Number of Records to be written: " + Records.size());
        System.out.println("Number of Threads: " + numThreads);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(URL);
        Stat stat = new Stat();
        final PostRecord postRecord = new PostRecord(webTarget, stat);

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        List<Stat> listStat = new ArrayList<>();

        for (Record record : Records) {
            pool.submit(() -> listStat.add(postRecord.doPost(record)));

        }

        pool.shutdown();

        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stats
        long endTime = System.currentTimeMillis();
        System.out.println("Time Taken: " + (endTime-startTime));
        statsOutput(startTime, endTime ,stat,numThreads, listStat);

    }

    public void getOneTask(int skierID, int dayNum) {
        long startTime = System.currentTimeMillis();

        String api = "/rest/myvert/" + skierID + "&" + dayNum;
        String url = "http://localhost:7070" + api;
        //String url = "http://34.214.91.35:8080/Assignment2-tomcat_war" + api;

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url);
        Stat stat = new Stat();

        RecordCollector recordCollector = new RecordCollector(webTarget, skierID, dayNum, client, stat);
        recordCollector.getVert().toString();

        client.close();

        long endTime = System.currentTimeMillis();
        statsOutput(startTime, endTime ,stat, 1, null);
    }


    public void getAllTasks(int numThreads){
        int dayNum =1;

        Client client = ClientBuilder.newClient();
        Stat stat = new Stat();

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Stat> listStat = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        System.out.println("Reading Skier Data...");

        System.out.println("Read Start time: " + new Date(startTime));
        for (int i = 0; i < numThreads; i++) {
            final int factor = i;
            pool.submit(() -> {
                for (int j = 1; j <= 40; j++) {
                    int skierID = 40 * factor + j;
                    String api = "/rest/myvert/" + skierID + "&" + dayNum;
                    String url = "http://localhost:7070" + api;
                    //String url = "http://34.214.91.35:8080/Assignment2-tomcat_war" + api;
                    WebTarget webTarget = client.target(url);
                    RecordCollector recordCollector = new RecordCollector(webTarget, skierID, dayNum, client, stat);
                    listStat.add(recordCollector.getVert());
                    //System.out.println(recordCollector.getVert().toString());
                }
            });
        }

        long endTime = System.currentTimeMillis();
        statsOutput(startTime, endTime ,stat, numThreads, listStat);
    }


    // Display all the statistics related to a particular request
    private void statsOutput(long startTime, long endTime ,Stat stat, int taskSize, List<Stat> listStat) {

        System.out.println();
        System.out.println("    !!! END OF REQUEST !!!");
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("     STATISTICS");
        System.out.println("----------------------------------");
        System.out.println("1. Number of threads: " + taskSize);
        System.out.println("2. Total run time: " + (endTime - startTime));
        System.out.println("3. Total request sent: " + stat.getSentRequestsNum());
        System.out.println("4. Total successful request: " + stat.getSuccessRequestsNum());
        System.out.println("5. Mean latency: " + stat.getMeanLatency());
        System.out.println("6. Median latency: " + stat.getMedianLatency());
        System.out.println("7. 95th percentile latency: " + stat.get95thLatency());
        System.out.println("8. 99th percentile latency: " + stat.get99thLatency());

        if(listStat!=null){
            OutputChart outputChart = new OutputChart(listStat);
            outputChart.generateChart("Part 1");
        }

    }






    public static void main(String[] args) {
        final MyClient myClient = new MyClient();

        /*
        * How to use the file :
        *  1. Uncomment myClient.postTasks(100) to load the data
        *  2. Uncomment myClient.singleGetTask(2,1) to get data for a particular skierID and dayNum
        *  3. Uncomment myClient.getTasks(1,100) to get all data for given dayNum
        *  *  NOTE : taskSize = Number of Threads in the threadpool
         */

        // read the .csv file data
        //myClient.readCsvFile(FILE_PATH);

        //myClient.postTasks(70);

        //myClient.getOneTask(1,1);

        //STEP 6

        myClient.readCsvFile(FILE_PATH2);
        myClient.getAllTasks(100);


        // Truncate the data present in the Table to start fresh
//       MyRecord myRecord = new MyRecord();
//        try {
//            int i = myRecord.truncateRecord();
//            System.out.println(i);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
} // end of class
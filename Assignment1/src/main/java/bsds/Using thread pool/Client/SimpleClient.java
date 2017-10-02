package bsds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleClient {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    // Input from console
    int numThreads;
    int numIterations;
    String serverIP;
    int portNumber;

    // Parse commandline arguments
    if (args.length == 4) {
      numThreads = Integer.parseInt(args[0]);
      numIterations = Integer.parseInt(args[1]);
      serverIP = args[2];
      portNumber = Integer.parseInt(args[3]);
    } else { // Set defaults
      serverIP = "http://localhost";
      numThreads = 10;
      numIterations = 100;
      portNumber = 7070;
    }

    System.out.println("Input values: " + numThreads + " " + numIterations + " " + serverIP + " " + portNumber);
    System.out.println("Client starting...time: " + System.currentTimeMillis()/1000);

    ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    // A list of tasks to be passed to the ExecutorService
    List<MyTask> listTasks = new ArrayList<>();
    for (int i = 0; i < numThreads; i++) {
      listTasks.add(new MyTask(numIterations, serverIP, portNumber));
    }

    // Threads started
    System.out.println("All threads running...");
    long startTime = System.currentTimeMillis();
    List<Future<Result>> futureResults = executor.invokeAll(listTasks);

    // Threads ended
    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS); // Blocks until all threads terminated
    long endTime = System.currentTimeMillis();
    while(!executor.isTerminated()) {}
      System.out.println("All threads completed...");

    System.out.println("All threads complete... time: " + System.currentTimeMillis());

    // Collecting results fom each of the Future returned from ExecutorService
    List<Result> results = new ArrayList<>();
    for(Future<Result> tr : futureResults)
      results.add(tr.get());
    StatGenerator stats = new StatGenerator(results);

    // Step 4
    System.out.println("Total number of requests sent: " + stats.getNumRequests());
    System.out.println("Total number of successful responses: " + stats.getNumSuccesses());
    System.out.println("Test Wall time: " + (endTime - startTime) / 1000 + " seconds");

    // Step 5
    System.out.println("Mean latency: " + stats.mean() + " milliseconds");
    System.out.println("Median latency: " + stats.median() + " milliseconds");
    System.out.println("99th percentile latency: " + stats.latencyPercentile(99) + " milliseconds");
    System.out.println("95th percentile latency: " + stats.latencyPercentile(95) + " milliseconds");

  }
}

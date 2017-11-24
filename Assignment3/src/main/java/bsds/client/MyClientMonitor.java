package bsds.client;

import bsds.dao.MetricsDB;
import bsds.util.StatUtil;


import java.io.IOException;
import java.util.List;

public class MyClientMonitor {

    private static String hostname;
    private boolean latency;
    private static boolean errorSet;
    private static boolean dbSet;
    private static boolean hostsSet;


    public void printStatistics(List<Long> statList) {
        // Print the stats
        System.out.println("    !!! END OF MONITOR TASK !!!");
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("     STATISTICS");
        System.out.println("----------------------------------");
        System.out.println("1. Median latency : " + StatUtil.findMedian(statList) + "ms");
        System.out.println("2. Mean latency :" + StatUtil.findMean(statList) + "ms:");
        System.out.println("3. 99th percentile :" + StatUtil.findPercentile(statList, 0.99) +"ms");
        System.out.println("4. 95th percentile :" + StatUtil.findPercentile(statList, 0.95) +"ms");

    }

    public static void main(String[] args) throws IOException {

        MyClientMonitor monitor = new MyClientMonitor();
        MetricsDB metricsDB = new MetricsDB();
        System.out.println("List of All Servers:");
        List<String> allhosts =  metricsDB.selectHosts();
        for (String hostname : allhosts) {
            System.out.println(hostname);
        }

        String statType = "LATENCY";

        if (errorSet) {
            System.out.println("Count of error in Requests : " + metricsDB.getErrorCount());
        } else {
                if (dbSet) {
                    statType = "DB";
                }

                if (hostsSet) {
                    List<Long> metricValues = metricsDB.getLatencyHosts(statType);
                    System.out.println("\nFinding median latency for server all servers : \nMyMetricsEnum type : " + statType);
                    monitor.printStatistics(metricValues);

                } else {
                    List<Long> metricValues = metricsDB.selectMetrics(hostname, statType);
                    System.out.println("\nFinding median latency for server : " + hostname + "\nMyMetricsEnum type : " + statType);
                    monitor.printStatistics(metricValues);
                }

            }
    }



}

package bsds.metrics;

import bsds.db.ConnectionManager;
import bsds.dao.MetricsDB;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class Monitor {
    private static final long DELAY_SECONDS = 5;

    private final ScheduledExecutorService executor;
    private final ReadWriteLock lock;
    private final MetricsDB metricsDB = new MetricsDB();
    private final Connection conn = ConnectionManager.makeConnection(ConnectionManager.URL, ConnectionManager.USER, ConnectionManager.PASSWORD );

    private Map<Thread, List<StatValues>> threadStats;

    public Monitor() {
        executor = Executors.newScheduledThreadPool(1);
        lock = new ReentrantReadWriteLock();
        threadStats = new ConcurrentHashMap<>();

        executor.scheduleWithFixedDelay(() -> {
            lock.writeLock().lock();
            Map<Thread, List<StatValues>> threadLocalMetricsCopy = threadStats;
            threadStats = new ConcurrentHashMap<>();
            lock.writeLock().unlock();

            threadLocalMetricsCopy.values().forEach(metricDataList ->
                    metricDataList.forEach(metricsDB::insertMetrics));
        }, DELAY_SECONDS, DELAY_SECONDS, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(DELAY_SECONDS, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    ConnectionManager.closeConnection(conn);
                }
            }
        }));    
    }

    public <T> T reportOperation(MyMetricsEnum errorMyMetricsEnum, MyMetricsEnum latencyMyMetricsEnum, Supplier<T> operation, Supplier<T> onError) {
        long startTime = System.currentTimeMillis();
        int errors = 0;
        try {
            return operation.get();
        } catch (Throwable t) {
            ++errors;
            return onError.get();
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            reportMetric(new StatValues(startTime, latencyMyMetricsEnum, latency));
            reportMetric(new StatValues(startTime, errorMyMetricsEnum, errors));
        }
    }

    private void reportMetric(StatValues statValues) {
        Thread currentThread = Thread.currentThread();
        lock.readLock().lock();
        List<StatValues> statValuesList = threadStats.getOrDefault(currentThread, new LinkedList<>());
        statValuesList.add(statValues);
        threadStats.put(currentThread, statValuesList);
        lock.readLock().unlock();
    }
}

package bsds.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/*
* Using JFreeChart to Plot the graph / Alternatively we could store the metrics onto a
* CSV file and visisualize the data
* */

public class OutputGraph {

    public static void plotGraph(String title, Map<Long, Long> latencyMap, long startTime, String task) {

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries ts = new TimeSeries("Time (ms)");
        latencyMap.forEach((time, latency) -> {
            long pointOfTime = time - startTime;
            ts.addOrUpdate(new Millisecond(new Date(pointOfTime)), latency);
        });
        timeSeriesCollection.addSeries(ts);
        String xLabel = "Time";
        String yLabel = "Latency";
        JFreeChart lineChart = ChartFactory.createXYLineChart(title, xLabel, yLabel, timeSeriesCollection,
                PlotOrientation.VERTICAL, true, true, false);
        try {
            String chartPath = "/Users/divyaagarwal/Desktop/BSDS/" + title + ".JPEG";
            ChartUtilities.saveChartAsJPEG(new File(chartPath), lineChart, 1200, 1000);
        } catch (IOException e) {
            System.err.println("Error in chart generation " + e);
        }
    }

}

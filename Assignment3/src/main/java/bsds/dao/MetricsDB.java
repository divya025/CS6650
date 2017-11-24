package bsds.dao;

import bsds.db.ConnectionManager;
import bsds.metrics.StatValues;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricsDB {

    // Table Name for Storing Metrics :- Metrics

    private Statement statement = null;
    private Connection connection = ConnectionManager.makeConnection(ConnectionManager.URL, ConnectionManager.USER, ConnectionManager.PASSWORD);
    private static final String HOST = getHostname();

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertMetrics(StatValues statValues) {
        try {
            String insertQuery = "INSERT into Metrics (timeTaken, host, types, typesValue) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setLong(1, statValues.getTimestamp());
            statement.setString(2, HOST);
            statement.setString(3, statValues.getMyMetricsEnum().name());
            statement.setLong(4, statValues.getValue());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> selectMetrics(String hostname, String metric) {
        try {
            String getLatencyQuery = "SELECT typesValue FROM Metrics WHERE host = ? AND types LIKE ? ORDER BY typesValue";
            PreparedStatement statement = connection.prepareStatement(getLatencyQuery);
            statement.setString(1, hostname);
            statement.setString(2, "%"+metric+"%");
            ResultSet rs = statement.executeQuery();
            List<Long> metricValue = new ArrayList<>();
            while(rs.next()) {
                metricValue.add(rs.getLong(1));
            }
            return metricValue;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public List<Long> getLatencyHosts(String metric) {
        try {
            String getLatencyQuery = "SELECT typesValue FROM Metrics WHERE metric LIKE ? ORDER BY metricValue";
            PreparedStatement medianValueStatement = connection.prepareStatement(getLatencyQuery);
            medianValueStatement.setString(1, "%"+metric+"%");
            ResultSet rs = medianValueStatement.executeQuery();
            List<Long> metricValue = new ArrayList<>();
            while(rs.next()) {
                metricValue.add(rs.getLong(1));
            }
            return metricValue;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public int getErrorCount() {
        try {
            int errorCount = 0;
            String getLatencyQuery = "SELECT count(typesValue) from Metrics WHERE types LIKE '%ERRORS%' AND typesValue > 0";
            PreparedStatement medianValueStatement = connection.prepareStatement(getLatencyQuery);
//          medianValueStatement.setString(1, hostname);
            ResultSet rs = medianValueStatement.executeQuery();
            while (rs.next()) {
                errorCount = rs.getInt(1);
            }
            return errorCount;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return 0;

    }

    public List<String> selectHosts() {
        try {
            String getHostnameQuery = "SELECT DISTINCT(host) FROM Metrics";
            PreparedStatement getHostnameStmt = connection.prepareStatement(getHostnameQuery);
            ResultSet rs = getHostnameStmt.executeQuery();
            List<String> hostList = new ArrayList<>();
            while (rs.next()) {
                hostList.add(rs.getString(1));
            }
            return hostList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
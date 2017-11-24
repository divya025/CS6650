package bsds.dao;

import bsds.db.ConnectionManager;
import bsds.metrics.Monitor;
import bsds.metrics.MyMetricsEnum;
import bsds.model.RecordData;
import bsds.model.JSONModel;
//import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//@RequiredArgsConstructor
public class RecordDB {

    // name of Table for All Skier Data :- Record
    private ConnectionManager connectionManager;
    private Monitor monitor;

    public RecordDB(ConnectionManager connectionManager, Monitor monitor) {
        this.connectionManager = connectionManager;
        this.monitor = monitor;
    }

    public void postData(RecordData data) throws SQLException {
        monitor.reportOperation(MyMetricsEnum.RECORD_POST_ERROR, MyMetricsEnum.RECORD_POST_LATENCY, () -> {
            try (Connection conn = connectionManager.getConnection()) {

                String insertQuery = "INSERT INTO Record(skierId, dayNum, liftId, resortId, time) VALUES (?, ?, ?, ?, ?);";
                PreparedStatement statement = conn.prepareStatement(insertQuery);

                statement.setInt(1, data.getSkierID());
                statement.setInt(2, data.getDayNum());
                statement.setInt(3, data.getLiftId());
                statement.setInt(4, data.getResortId());
                statement.setInt(5, data.getTime());
                statement.executeUpdate();
                statement.close();

                // Uncomment to validate for recordID
//                ResultSet results;
//                results = statement.getGeneratedKeys();
//                int recordID = -1;
//
//                if (results.next()) {
//                    recordID = results.getInt(1);
//                    System.out.println(recordID);
//                } else {
//                    throw new SQLException("Unable to retrieve auto-generated key.");
//                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }, () -> null);
    }

    public JSONModel getSkierStats(int skierId, int dayNum) throws SQLException {
        return monitor.reportOperation(MyMetricsEnum.METRICS_GET_ERROR, MyMetricsEnum.METRICS_GET_LATENCY, () -> {
            try (Connection conn = connectionManager.getConnection()) {

                String queryString = "SELECT liftId FROM Record WHERE skierId = ? AND dayNum = ?;";
                PreparedStatement queryStatement = conn.prepareStatement(queryString);

                System.out.println(queryString);

                queryStatement.setInt(1, skierId);
                queryStatement.setInt(2, dayNum);
                ResultSet resultSet = queryStatement.executeQuery();
                int liftRides = 0;
                int verticalMeters = 0;
                while (resultSet.next()) {
                    ++liftRides;
                    verticalMeters += getVerticalMetersForLift(resultSet.getInt("liftId"));
                }
                queryStatement.close();
                return new JSONModel(skierId, dayNum, verticalMeters, liftRides);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> null);
    }

    private int getVerticalMetersForLift(int liftId) {
        if (liftId < 1 || liftId > 40) {
            return 0;
        }

        if (liftId > 30) {
            return 500;
        }
        if (liftId > 20) {
            return 400;
        }
        if (liftId > 10) {
            return 300;
        }
        return 200;
    }
}

package bsds.dao;

import bsds.model.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CREATE/SELECT/DELETE a Record (entry) in the local Database schema.
 * Using SQL commands to perform the given operations.
 */
public class MyRecord {

    ConnectionManager connectionManager;

    // Private to only this class cannot be accessed by other class
    private static MyRecord myRecord = null;

    // Constructor: Establish a connection to the DB when MyRecord is instantiated
    private MyRecord() {

        connectionManager = new ConnectionManager();
    }

    // Getter for the instance of the class
    public static MyRecord getMyRecord() {
        if(myRecord == null) {
            myRecord = new MyRecord();
        }
        return myRecord;
    }


    public Record postRecord(Record record) throws SQLException {
        String insertQuery = "INSERT Record(SkierID,LiftID,DayNum) VALUE (?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet results = null;
        try {
            connection = connectionManager.getConnection();

            insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, record.getSkierID());
            insertStmt.setInt(2, record.getLiftID());
            insertStmt.setInt(3, record.getDayNum());
            insertStmt.executeUpdate();

            results = insertStmt.getGeneratedKeys();
            int recordID = -1;

            if (results.next()) {
                recordID = results.getInt(1);
            } else {
                throw new SQLException("Unable to retrieve auto-generated key.");
            }
            record.setRecordID(recordID);
            return record;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
            if (results != null) {
                results.close();
            }
        }
    }

    // Select a record given the skierID and DayNum
    public List<Record> getRecordForSkier( int skierID, int dayNum) throws SQLException {
        List<Record> records = new ArrayList<Record>();
        String selectQuery = "SELECT * FROM Record WHERE SkierID=? AND DayNum=?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, skierID);
            selectStmt.setInt(2, dayNum);
            results = selectStmt.executeQuery();
            while(results.next()) {
                int recordID = results.getInt("RecordID");
                int liftID = results.getInt("LiftID");

                Record record = new Record(recordID, skierID, liftID, dayNum);
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return records;
    }


    // Delete the record given the RecordID(Primary Key)
    public Record deleteRecord(Record record) throws SQLException{
        String deleteQuery = "DELETE FROM Record WHERE RecordID=?;";
        Connection connection = null;
        PreparedStatement deleteStmt = null;
        try {
            connection = connectionManager.getConnection();
            deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, record.getRecordID());
            deleteStmt.executeUpdate();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(deleteStmt != null) {
                deleteStmt.close();
            }
        }
    }

    public int truncateRecord() throws SQLException{
        String truncateQuery = "TRUNCATE TABLE Record";
        Connection connection = null;
        PreparedStatement truncateStmt = null;
        int result =0;
        try{
            connection = connectionManager.getConnection();
            truncateStmt = connection.prepareStatement(truncateQuery);
            result = truncateStmt.executeUpdate();
            return result;
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }finally {
            if(connection!=null)
                connection.close();
            if(truncateStmt!=null)
                truncateStmt.close();
        }
    }

}

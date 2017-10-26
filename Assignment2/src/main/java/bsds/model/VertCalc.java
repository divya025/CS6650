package bsds.model;

import bsds.dao.MyRecord;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import bsds.dao.ConnectionManager;
import com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection;

/**
 * Calculate the number of lifts and Vertical height given a skierID and DayNum
 */
public class VertCalc {

    Connection connection;

    private static VertCalc vertCalc;

    private List<Record> cache = new ArrayList<Record>();
    // the number of lift verticals is a constant
    private final int[] LIFT_VERTICAL_RISE = {200, 300, 400, 500};

    // Constructor
    public VertCalc() {
        try {
            connection = new ConnectionManager().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Instanciation
    public static synchronized VertCalc createObject() {
        if(vertCalc == null) {
            vertCalc = new VertCalc();
        }
        return vertCalc;
    }

    public synchronized List<Record> getCache() {
        return this.cache;
    }

    public synchronized void addRecordToCache(Record record) {
        cache.add(record);
    }

    public synchronized int getCacheSize() {
        return this.cache.size();
    }


    public Vertical getMyVertical(int skierID, int dayNum) {
        List<Record> records = new ArrayList<Record>();
        try {
            records = MyRecord.getMyRecord().getRecordForSkier(skierID, dayNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int liftTimes = records.size();
        int totalVertical = 0;
        for (int i = 0; i < liftTimes; i++) {
            totalVertical += getEachVertical(records.get(i).getLiftID());
        }
        return new Vertical(totalVertical,liftTimes);
    }

    // Helper method to getMyVertical
    // To find the vertical gain based on liftID
    private int getEachVertical(int liftID) {
        if (liftID <= 10) {
            return LIFT_VERTICAL_RISE[0];
        }else if (liftID <= 20) {
            return LIFT_VERTICAL_RISE[1];
        }else if (liftID <= 30) {
            return LIFT_VERTICAL_RISE[2];
        }else{
            return LIFT_VERTICAL_RISE[3];
        }
    }
}

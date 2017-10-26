package bsds.model;

/**
 * Record is an entry in the database, all the members of the schema Record
 * ADT of Record
 */
public class Record {

    private int recordID;
    private int skierID;
    private int liftID;
    private int dayNum;


    public Record() {
    }

    public Record(int recordID) {
        this.recordID = recordID;
    }

    // Used to intialize instance of Record while reading data from file
    public Record(int skierID, int liftID, int dayNum) {
        this.skierID = skierID;
        this.liftID = liftID;
        this.dayNum = dayNum;

    }

    public Record(int recordID, int skierID, int liftID, int dayNum) {
        this.recordID = recordID;
        this.skierID = skierID;
        this.liftID = liftID;
        this.dayNum = dayNum;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getSkierID() {
        return skierID;
    }

    public void setSkierID(int skierID) {
        this.skierID = skierID;
    }

    public int getLiftID() {
        return liftID;
    }

    public void setLiftID(int liftID) {
        this.liftID = liftID;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) { this.dayNum = dayNum; }

    @Override
    public String toString() {
        return "SkierID: " + this.skierID + "  LiftID: " + this.liftID + "  DayNum: "
                + this.dayNum;
    }
}

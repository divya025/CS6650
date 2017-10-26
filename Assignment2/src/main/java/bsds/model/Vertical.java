package bsds.model;

/**
 *  Getter and setters for Vertical height.
 */
public class Vertical {
    int totalVertical;
    int liftTimes;

    public Vertical() { }

    public Vertical(int totalVertical, int liftTimes) {
        this.totalVertical = totalVertical;
        this.liftTimes = liftTimes;
    }

    public int getTotalVertical() {

        return totalVertical;
    }

    public void setTotalVertical(int totalVertical) {
        this.totalVertical = totalVertical;
    }

    public int getLiftTimes() {

        return liftTimes;
    }

    public void setLiftTimes(int liftTimes) {

        this.liftTimes = liftTimes;
    }

    public String toString() {
        return "Total vertical: " + totalVertical + "  Number of lift ride: " + liftTimes;
    }
}

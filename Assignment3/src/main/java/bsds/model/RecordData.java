package bsds.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;

import java.io.Serializable;

/**
 *
 * Simple class to wrap the data in a RFID lift pass reader record
 */
public class RecordData implements Serializable, Comparable<RecordData>  {
    private static final Gson GSON = new Gson();

    private int resortId;
    private int dayNum;
    private int skierId;
    private int liftId;
    private int time;

    public RecordData(int resortId, int dayNum, int skierID, int liftId, int time) {
        this.resortId = resortId;
        this.dayNum = dayNum;
        this.skierId = skierID;
        this.liftId = liftId;
        this.time = time;
    }

    public int getResortId() {
        return resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public int getSkierID() {
        return skierId;
    }

    public void setSkierID(int skierID) {
        this.skierId = skierID;
    }

    public int getLiftId() {
        return liftId;
    }

    public void setLiftId(int liftId) {
        this.liftId = liftId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public int compareTo(RecordData compareData) {
        int compareTime = compareData.getTime();

        //ascending order
        return this.time - compareTime ;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static RecordData fromJson(String json) {
        return GSON.fromJson(json, RecordData.class);
    }
}
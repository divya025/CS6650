package bsds.model;

import com.google.gson.Gson;


import java.io.Serializable;


public class JSONModel implements Serializable {

    private static final Gson GSON = new Gson();

    private int skierId;
    private int dayNum;
    private int totalVerticalMetres;
    private int totalLiftRides;

    public JSONModel(int skierId, int dayNum, int verticalMeters, int liftRides) {
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.totalVerticalMetres = verticalMeters;
        this.totalLiftRides = liftRides;

    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static JSONModel fromJson(String json) {
        return GSON.fromJson(json, JSONModel.class);
    }

}

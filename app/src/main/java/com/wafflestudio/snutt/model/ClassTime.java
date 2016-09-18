package com.wafflestudio.snutt.model;

import com.google.gson.JsonObject;

/**
 * Created by makesource on 2016. 2. 10..
 */
public class ClassTime {
    private int day;
    private int start;
    private int len;
    private String place;
    private String _id;

    public ClassTime(int day, int start, int len, String place) {
        this.day = day;
        this.start = start;
        this.len = len;
        this.place = place;
    }

    public ClassTime(JsonObject jsonObject) {
        this.day = jsonObject.get("day").getAsInt();
        this.start = jsonObject.get("start").getAsInt();
        this.len = jsonObject.get("len").getAsInt();
        this.place = jsonObject.get("place").getAsString();
        //this._id = jsonObject.get("_id").getAsString();
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}

package com.example.overdo.geoinfocollecter.entities;

import java.io.Serializable;

/**
 * Created by Overdo on 2017/1/26.
 */

public class GeoInfo implements Serializable {
    private static String Charger;
    private static String Collector;
    private static String Date;
    private static String Address;
    private static String Note;
    private static double Latitude;
    private static double Longtitude;
    private static double Elevation;

    public String getCharger() {
        return Charger;
    }

    public void setCharger(String charger) {
        Charger = charger;
    }

    public String getCollector() {
        return Collector;
    }

    public void setCollector(String collector) {
        Collector = collector;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
    }

    public double getElevation() {
        return Elevation;
    }

    public void setElevation(double elevation) {
        Elevation = elevation;
    }


}

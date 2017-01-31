package com.example.overdo.geoinfocollecter.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Overdo on 2017/1/31.
 */
public class Pic extends DataSupport {

    private GeoInfo mGeoInfo;

    private String photoPath;


    public GeoInfo getGeoInfo() {
        return mGeoInfo;
    }

    public void setGeoInfo(GeoInfo geoInfo) {
        mGeoInfo = geoInfo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}

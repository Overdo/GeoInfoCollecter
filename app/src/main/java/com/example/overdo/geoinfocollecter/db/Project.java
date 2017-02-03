package com.example.overdo.geoinfocollecter.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Overdo on 2017/1/31.
 */

public class Project extends DataSupport implements Serializable {


    private long id;

    private String leader;
    private String collector;
    private String projectname;
    private List<GeoInfo> geoinfos = new ArrayList<GeoInfo>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public List<GeoInfo> getGeoinfos() {
        return geoinfos;
    }

    public void setGeoinfos(List<GeoInfo> geoinfos) {
        this.geoinfos = geoinfos;
    }

    public String getLeader() {

        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

}

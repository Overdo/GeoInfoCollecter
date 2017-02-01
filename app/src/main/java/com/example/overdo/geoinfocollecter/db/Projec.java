package com.example.overdo.geoinfocollecter.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Overdo on 2017/1/31.
 */

public class Projec extends DataSupport implements Serializable {


    private long id;

    private String leader;
    private String collector;
    private String projectname;
    private List<GeoInfo> infos = new ArrayList<>();


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


    public List<GeoInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<GeoInfo> infos) {
        this.infos = infos;
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

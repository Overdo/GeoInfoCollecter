package com.example.overdo.geoinfocollecter.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Overdo on 2017/1/31.
 */

public class Projec extends DataSupport implements Serializable {


    private String leader;
    private String collector;

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    private String project_name;

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

    private List<GeoInfo> projects = new ArrayList<GeoInfo>();

    public List<GeoInfo> getProjects() {
        return projects;
    }

    public void setProjects(List<GeoInfo> projects) {
        this.projects = projects;
    }
}

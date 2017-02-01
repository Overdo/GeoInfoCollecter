package com.example.overdo.geoinfocollecter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.App;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.db.Project;

import java.util.List;

/**
 * Created by Overdo on 2017/2/1.
 */

public class ProjectAdapter extends BaseAdapter {

    private List<Project> mProjectList;

    public ProjectAdapter(List<Project> list) {
        this.mProjectList = list;
    }

    @Override
    public int getCount() {
        return mProjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(App.getContext()).inflate(R.layout.item_projectlist, null);
            holder = new viewHolder();
            holder.projectName = (TextView) convertView.findViewById(R.id.item_projectname);
            holder.projectLeader = (TextView) convertView.findViewById(R.id.item_projectleader);
            holder.projectCollector = (TextView) convertView.findViewById(R.id.item_projectcollector);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.projectName.setText("项目名称：" + mProjectList.get(position).getProjectname());
        holder.projectLeader.setText("负责人：" + mProjectList.get(position).getLeader());
        holder.projectCollector.setText("记录人：" + mProjectList.get(position).getCollector());


        return convertView;
    }

    class viewHolder {
        TextView projectName, projectLeader, projectCollector;
    }

}

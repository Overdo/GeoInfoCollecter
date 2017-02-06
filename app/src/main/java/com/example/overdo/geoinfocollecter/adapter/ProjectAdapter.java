package com.example.overdo.geoinfocollecter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.db.Project;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;

import java.util.List;

public class ProjectAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Project> mProjectList;
    private IOnItemClickListener mClickListener;

    public ProjectAdapter(Context mContext, List<Project> projectList, IOnItemClickListener listener) {
        this.mContext = mContext;
        this.mProjectList = projectList;
        this.mClickListener = listener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_projectlist, null);
        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView projectName = (TextView) convertView.findViewById(R.id.item_projectname);
        TextView projectLeader = (TextView) convertView.findViewById(R.id.item_projectleader);
        TextView projectCollector = (TextView) convertView.findViewById(R.id.item_projectcollector);
        projectName.setText("项目：" + mProjectList.get(position).getProjectname());
        projectLeader.setText("负责：" + mProjectList.get(position).getLeader());
        projectCollector.setText("  记录：" + mProjectList.get(position).getCollector());

        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
//                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_normal);
        Button deleteBtn = (Button) convertView.findViewById(R.id.delete);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onclick(position);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.ondelete(position);
                swipeLayout.close();
            }
        });
    }

    @Override
    public int getCount() {
        return mProjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}

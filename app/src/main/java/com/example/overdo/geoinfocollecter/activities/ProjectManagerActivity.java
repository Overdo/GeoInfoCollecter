package com.example.overdo.geoinfocollecter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.db.Projec;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Overdo on 2017/1/29.
 */
public class ProjectManagerActivity extends BaseActivity {

    @InjectView(R.id.lv_projects)
    SlideAndDragListView mLvProjects;
    private List<Projec> mProjectList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        ButterKnife.inject(this);
        initToolbar();

        intiListview();

    }

    private void intiListview() {

        Menu menu = new Menu(true, true);
        menu.addItem(new MenuItem.Builder()
                .setWidth(100)
                .setText("删除")
                .setTextColor(Color.RED)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
        mLvProjects.setMenu(menu);

        mProjectList = DataSupport.findAll(Projec.class);

        if (mProjectList.isEmpty()) {
            showToast("空数据库");
        } else {

            mLvProjects.setAdapter(mAdapter);
        }


    }

    private BaseAdapter mAdapter = new BaseAdapter() {
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
                convertView = LayoutInflater.from(ProjectManagerActivity.this).inflate(R.layout.projectlist_item, null);
                holder = new viewHolder();
                holder.projectName = (TextView) convertView.findViewById(R.id.item_projectname);
                holder.projectLeader = (TextView) convertView.findViewById(R.id.item_projectleader);
                holder.projectCollector = (TextView) convertView.findViewById(R.id.item_projectcollector);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }

            holder.projectName.setText(mProjectList.get(position).getProjectname());
            holder.projectLeader.setText("负责：" + mProjectList.get(position).getLeader());
            holder.projectCollector.setText("采集：" + mProjectList.get(position).getCollector());

            holder.projectName.setOnClickListener(mOnClickListener);
            return convertView;

        }

        class viewHolder {
            TextView projectName,projectLeader,projectCollector;

        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o instanceof Integer) {
                    Toast.makeText(ProjectManagerActivity.this, "button click-->" + ((Integer) o), Toast.LENGTH_SHORT).show();
                }
            }
        };
    };


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("项目管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

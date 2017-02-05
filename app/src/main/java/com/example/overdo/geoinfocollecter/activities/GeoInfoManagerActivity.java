package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.GeoInfoAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.db.Project;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Overdo on 2017/2/1.
 */

public class GeoInfoManagerActivity extends BaseActivity {

    @InjectView(R.id.lv_geoinfos)
    ListView mLvGeoinfos;
    @InjectView(R.id.tv_nodata)
    TextView mTvNodata;
    private List<GeoInfo> mGeoInfoList;
    private GeoInfoAdapter mAdapter;
    private Project mProject;
    private static final String TAG = "GeoInfoManagerActivity";
    private List<Project> mPlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geoinfo_manager);
        ButterKnife.inject(this);

        initToolbar();
        initData();
        intiView();

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("点位管理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initData() {
        mProject = (Project) getIntent().getSerializableExtra("geoinfo_list");

    }


    private void intiView() {
        mGeoInfoList = DataSupport.findAll(GeoInfo.class);
        final List<GeoInfo> mlist = DataSupport.where("project_id = ?", mProject.getId() + "").find(GeoInfo.class);
        mPlist = DataSupport.where("id = ?", mProject.getId() + "").find(Project.class);


        if (null == mGeoInfoList || mGeoInfoList.isEmpty()) {
            mLvGeoinfos.setVisibility(View.GONE);
            mTvNodata.setVisibility(View.VISIBLE);
        } else {
            mLvGeoinfos.setVisibility(View.VISIBLE);
            mTvNodata.setVisibility(View.GONE);

            mAdapter = new GeoInfoAdapter(this, mlist, new IOnItemClickListener() {
                @Override
                public void onclick(int position) {
                    Intent intent = new Intent(GeoInfoManagerActivity.this, PointDetailActivity.class);
                    intent.putExtra("geo_info", mlist.get(position));
                    intent.putExtra("project_info",mPlist.get(0));
                    startActivity(intent);
                }

                @Override
                public void ondelete(int position) {
                    DataSupport.delete(GeoInfo.class, mlist.get(position).getId());
                    mlist.remove(position);
                    mAdapter.notifyDataSetChanged();
                    showToast("删除成功");
                }
            });

            mLvGeoinfos.setAdapter(mAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.geo_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_point:
                Intent intent = new Intent(GeoInfoManagerActivity.this, PointDetailActivity.class);

                intent.putExtra("project_info",mPlist.get(0));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.GeoInfoAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geoinfo_manager);
        ButterKnife.inject(this);

        initToolbar();
        initData();
        intiView();

    }

    private void initData() {
        mGeoInfoList = getIntent().getParcelableExtra("geoinfo_list");
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

    private void intiView() {
        mGeoInfoList = DataSupport.findAll(GeoInfo.class);

        if (mGeoInfoList.isEmpty()) {
            mLvGeoinfos.setVisibility(View.GONE);
            mTvNodata.setVisibility(View.VISIBLE);
        } else {
            mLvGeoinfos.setVisibility(View.VISIBLE);
            mTvNodata.setVisibility(View.GONE);

            mAdapter = new GeoInfoAdapter(this,mGeoInfoList, new IOnItemClickListener() {
                @Override
                public void onclick(int position) {
                    Intent intent = new Intent(GeoInfoManagerActivity.this, PointDetailActivity.class);
                    intent.putExtra("geo_info", mGeoInfoList.get(position));
                    startActivity(intent);
                }

                @Override
                public void ondelete(int position) {
                    DataSupport.delete(GeoInfo.class, mGeoInfoList.get(position).getId());
                    mGeoInfoList.remove(position);
                    mAdapter.notifyDataSetChanged();
                    showToast("删除成功");
                }
            });

            mLvGeoinfos.setAdapter(mAdapter);
        }
    }
}

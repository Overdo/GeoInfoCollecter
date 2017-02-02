package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.GeoInfosAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.listener.IOnItemClickListener;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.yydcdut.sdlv.Menu.ITEM_NOTHING;

/**
 * Created by Overdo on 2017/2/1.
 */

public class GeoInfoManagerActivity extends BaseActivity implements SlideAndDragListView.OnMenuItemClickListener {

    @InjectView(R.id.lv_geoinfos)
    SlideAndDragListView mLvGeoinfos;
    @InjectView(R.id.tv_nodata)
    TextView mTvNodata;
    private List<GeoInfo> mGeoInfoList;
    private GeoInfosAdapter mAdapter;

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
            Menu menu = new Menu(true, true);
            menu.addItem(new MenuItem.Builder()
                    .setWidth(100)
                    .setText("删除")
                    .setTextColor(Color.RED)
                    .setDirection(MenuItem.DIRECTION_RIGHT)
                    .build());

            mLvGeoinfos.setMenu(menu);
            mAdapter = new GeoInfosAdapter(mGeoInfoList, new IOnItemClickListener() {
                @Override
                public void onclick(int position) {
                    Intent intent = new Intent(GeoInfoManagerActivity.this, GeoInfoDetailActivity.class);
                    intent.putExtra("geoinfo_list", mGeoInfoList.get(position));
                    startActivity(intent);
                }
            });

            mLvGeoinfos.setAdapter(mAdapter);
            mLvGeoinfos.setOnMenuItemClickListener(this);
        }
    }


    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        DataSupport.delete(GeoInfo.class, mGeoInfoList.get(itemPosition).getId());
        mGeoInfoList.remove(itemPosition);
        mAdapter.notifyDataSetChanged();
        showToast("删除成功");

        return ITEM_NOTHING;
    }



}

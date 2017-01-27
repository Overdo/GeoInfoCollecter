package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.entities.GeoInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Overdo on 2017/1/26.
 */

public class PointDetailActivity extends BaseActivity {

    @InjectView(R.id.tv_charger)
    TextView mTvCharger;
    @InjectView(R.id.tv_collector)
    TextView mTvCollector;
    @InjectView(R.id.tv_date)
    TextView mTvDate;
    @InjectView(R.id.tv_code)
    TextView mTvCode;
    @InjectView(R.id.tv_location)
    TextView mTvLocation;
    @InjectView(R.id.tv_lat)
    TextView mTvLat;
    @InjectView(R.id.tv_lng)
    TextView mTvLng;
    @InjectView(R.id.ibtn_add_photo)
    ImageButton mIbtnAddPhoto;
    @InjectView(R.id.ibtn_add_pic)
    ImageButton mIbtnAddPic;
    @InjectView(R.id.btn_cancel)
    Button mBtnCancel;
    @InjectView(R.id.btn_confirm)
    Button mBtnConfirm;
    private GeoInfo mGeoInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointdetail);
        ButterKnife.inject(this);

        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mGeoInfo = (GeoInfo) intent.getSerializableExtra("geo_info");
        mTvDate.setText("日期：" + mGeoInfo.getDate());
        mTvLocation.setText("地址：" + mGeoInfo.getAddress());
        mTvLat.setText("latitude：" + mGeoInfo.getLatitude());
        mTvLng.setText("longtitude：" + mGeoInfo.getLongtitude());


    }

    @OnClick({R.id.ibtn_add_photo, R.id.ibtn_add_pic, R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_add_photo:
                break;
            case R.id.ibtn_add_pic:
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                //保存信息后finish
                break;
        }
    }
}

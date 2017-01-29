package com.example.overdo.geoinfocollecter.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.overdo.geoinfocollecter.App;
import com.example.overdo.geoinfocollecter.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.example.overdo.geoinfocollecter.entities.Constants.NORMAL_MAP;
import static com.example.overdo.geoinfocollecter.entities.Constants.SATELITE_MAP;
import static com.example.overdo.geoinfocollecter.entities.Constants.TRAFFIC_MAP;

/**
 * Created by Overdo on 2017/1/29.
 */
public class SettingActivity extends BaseActivity {
    @InjectView(R.id.cib_normal)
    CheckableImageButton mCibNormal;
    @InjectView(R.id.cib_satelite)
    CheckableImageButton mCibSatelite;
    @InjectView(R.id.cib_traffic)
    CheckableImageButton mCibTraffic;
    private SharedPreferences mConfig;
    private String mapMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        initToolbar();
        initMapMode();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("天网弹道跟踪系统");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.cib_normal, R.id.cib_satelite, R.id.cib_traffic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cib_normal:
                if (!mCibNormal.isChecked()) {
                    mCibTraffic.setChecked(false);
                    mCibSatelite.setChecked(false);
                    mCibNormal.setChecked(true);
                }
                saveConfig("map_mode", NORMAL_MAP);
                break;
            case R.id.cib_satelite:
                if (!mCibSatelite.isChecked()) {
                    mCibTraffic.setChecked(false);
                    mCibNormal.setChecked(false);
                    mCibSatelite.setChecked(true);
                }
                saveConfig("map_mode", SATELITE_MAP);
                break;
            case R.id.cib_traffic:
                if (!mCibTraffic.isChecked()) {
                    mCibNormal.setChecked(false);
                    mCibSatelite.setChecked(false);
                    mCibTraffic.setChecked(true);
                }
                saveConfig("map_mode", TRAFFIC_MAP);
                break;
        }
    }


    private void initMapMode() {
        if (mConfig == null) {
            mConfig = App.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mapMode = mConfig.getString("map_mode", NORMAL_MAP);
        switch (mapMode) {
            case NORMAL_MAP:
                mCibNormal.setChecked(true);
                break;
            case SATELITE_MAP:
                mCibSatelite.setChecked(true);
                break;
            case TRAFFIC_MAP:
                mCibTraffic.setChecked(true);
                break;
        }
    }

}

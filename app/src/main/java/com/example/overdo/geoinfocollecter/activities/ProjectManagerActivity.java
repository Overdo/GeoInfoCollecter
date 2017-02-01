package com.example.overdo.geoinfocollecter.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.overdo.geoinfocollecter.R;

import butterknife.InjectView;

/**
 * Created by Overdo on 2017/1/29.
 */
public class ProjectManagerActivity extends BaseActivity {

    CardView c_dp, c_dev, c_mdt;
    @InjectView(R.id.iv_github)
    ImageView mIvGithub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        initToolbar();

    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("点位详情");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

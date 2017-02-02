package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.example.overdo.geoinfocollecter.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Overdo on 2017/1/29.
 */
public class AboutActivity extends BaseActivity {

    CardView c_dev, c_mdt;
    @InjectView(R.id.iv_github)
    ImageView mIvGithub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        c_dev = (CardView) findViewById(R.id.card_devaccount);

        c_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://guanjiancheng.xyz/about/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @OnClick(R.id.iv_github)
    public void onClick() {
        String url = "https://github.com/Overdo/GeoInfoCollecter";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}

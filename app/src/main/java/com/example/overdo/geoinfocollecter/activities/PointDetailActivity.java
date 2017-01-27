package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.PhotoAdapter;
import com.example.overdo.geoinfocollecter.entities.GeoInfo;
import com.example.overdo.geoinfocollecter.listener.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

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
    @InjectView(R.id.ibtn_add_pic)
    ImageButton mIbtnAddPic;
    @InjectView(R.id.btn_cancel)
    Button mBtnCancel;
    @InjectView(R.id.btn_confirm)
    Button mBtnConfirm;
    private GeoInfo mGeoInfo;


    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointdetail);
        ButterKnife.inject(this);

        getIntentData();
        initRecyclerView();
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

    @OnClick({ R.id.ibtn_add_pic, R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
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

    private void initRecyclerView(){

        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);

        RecyclerView rv_photoes = (RecyclerView) findViewById(R.id.rv_photoes);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        rv_photoes.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        rv_photoes.setAdapter(photoAdapter);

        rv_photoes.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(PhotoAdapter.MAX)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(false)
                                    .setSelected(selectedPhotos)
                                    .start(PointDetailActivity.this);
                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(PointDetailActivity.this);
                        }
                    }
                }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }
}

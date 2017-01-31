package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.PhotoAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
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
    @InjectView(R.id.tv_location)
    TextView mTvLocation;
    @InjectView(R.id.tv_lat)
    TextView mTvLat;
    @InjectView(R.id.tv_lng)
    TextView mTvLng;
    @InjectView(R.id.btn_cancel)
    Button mBtnCancel;
    @InjectView(R.id.btn_confirm_save)
    Button mBtnConfirm;
    @InjectView(R.id.ibtn_add_photo)
    ImageButton mIbtnAddPhoto;
    @InjectView(R.id.tv_code)
    EditText mTvCode;
    private GeoInfo intentData;


    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointdetail);
        ButterKnife.inject(this);
        initToolbar();
        getIntentData();
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

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        intentData = (GeoInfo) intent.getSerializableExtra("geo_info");
        mTvDate.setText(intentData.getDate());
        mTvLocation.setText("地址：" + intentData.getAddress());
        mTvLat.setText("latitude：" + intentData.getLatitude());
        mTvLng.setText("longtitude：" + intentData.getLongtitude());
        mTvCharger.setText(getProjectConfig("projection_charger"));
        mTvCollector.setText(getProjectConfig("projection_collector"));

    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm_save, R.id.ibtn_add_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_add_photo:
                initRecyclerView();
                mIbtnAddPhoto.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm_save:
                //保存信息后finish

                GeoInfo geoInfo = new GeoInfo();
                geoInfo.setLatitude(intentData.getLatitude());
                geoInfo.setAddress(intentData.getAddress());
                geoInfo.setLongtitude(intentData.getLongtitude());
                geoInfo.setDate(intentData.getDate());
                geoInfo.setCode(mTvCode.getText().toString());


                geoInfo.saveThrows();
                if (geoInfo.isSaved()) {
                    showToast("save succees");
                } else {
                    showToast("failed");
                }

                break;
        }
    }

    private void initRecyclerView() {

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

package com.example.overdo.geoinfocollecter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.overdo.geoinfocollecter.R;
import com.example.overdo.geoinfocollecter.adapter.PhotoAdapter;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.db.Project;
import com.example.overdo.geoinfocollecter.listener.RecyclerItemClickListener;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @InjectView(R.id.tv_date)
    EditText mTvDate;
    @InjectView(R.id.tv_location)
    EditText mTvLocation;
    @InjectView(R.id.tv_lat)
    EditText mTvLat;
    @InjectView(R.id.tv_lng)
    EditText mTvLng;
    @InjectView(R.id.btn_cancel)
    Button mBtnCancel;
    @InjectView(R.id.btn_confirm_save)
    Button mBtnConfirm;
    @InjectView(R.id.ibtn_add_photo)
    ImageButton mIbtnAddPhoto;
    @InjectView(R.id.tv_project_name)
    EditText mTvProjectName;
    @InjectView(R.id.tv_charger)
    EditText mTvLeader;
    @InjectView(R.id.tv_collector)
    EditText mTvCollector;
    @InjectView(R.id.tv_code)
    EditText mTvCode;
    @InjectView(R.id.tv_height)
    EditText mTvHeight;
    @InjectView(R.id.tv_note)
    EditText mTvNote;
    private GeoInfo intentGeoinfoData;
    private static final String TAG = "PointDetailActivity";

    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private Project mProject;
    private Project intentProjectData;
    private List<Project> mProjectData;
    private Project mIntentManagerProjectData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointdetail);
        ButterKnife.inject(this);

        initToolbar();
        initData();
        initRecyclerView();
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

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        intentGeoinfoData = (GeoInfo) intent.getSerializableExtra("geo_info");
        intentProjectData = (Project) intent.getSerializableExtra("project_info");
        mIntentManagerProjectData = (Project) intent.getSerializableExtra("project_info_from_manager");

        mProjectData = DataSupport.findAll(Project.class);

        initProjectEditext();

        if (intentGeoinfoData == null) {
            mTvDate.setText(getFormattime());
            mTvLocation.setText("");
            mTvHeight.setText("");
            mTvCode.setText("");
            mTvLng.setText("");
            mTvNote.setText("");
            return;
        }


        selectedPhotos = (ArrayList<String>) intentGeoinfoData.getPics();

        mTvDate.setText(intentGeoinfoData.getDate());
        mTvLocation.setText("" + intentGeoinfoData.getAddress());
        mTvLat.setText(intentGeoinfoData.getLatitude() + "");
        mTvLng.setText("" + intentGeoinfoData.getLongtitude());

        mTvNote.setText(intentGeoinfoData.getNote() == null ? "" : intentGeoinfoData.getNote());
        mTvHeight.setText(intentGeoinfoData.getElevation() == null ? "" : intentGeoinfoData.getElevation());
        mTvCode.setText(intentGeoinfoData.getCode() == null ? "" : intentGeoinfoData.getCode());


    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm_save, R.id.ibtn_add_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_add_photo:
                initPhotoPicker();
                mIbtnAddPhoto.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm_save:
                saveGeoInfos();
                break;
        }
    }

    private void initRecyclerView() {


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

    private void initPhotoPicker() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        intentGeoinfoData = null;
        intentProjectData = null;
    }

    private void saveGeoInfos() {

        //获取信息
        String projectname = mTvProjectName.getText().toString();
        String projectLeader = mTvLeader.getText().toString();
        String projectCollefctor = mTvCollector.getText().toString();
        String code = mTvCode.getText().toString();
        String date = mTvDate.getText().toString();
        String elevation = mTvHeight.getText().toString();
        String note = mTvNote.getText().toString();
        String location = mTvLocation.getText().toString();
        String latitude = mTvLat.getText().toString();
        String longtitude = mTvLng.getText().toString();


        if (TextUtils.isEmpty(projectname) ||
                TextUtils.isEmpty(projectLeader) || TextUtils.isEmpty(projectCollefctor)
                || TextUtils.isEmpty(code)) {
            showToast("前四项别留空");
            return;
        }

        //保存信息
        List<Project> projects = DataSupport.select("projectname").where("projectname = ?", projectname).find(Project.class);

        if (projects.isEmpty()) {
            mProject = new Project();
            mProject.setCollector(projectCollefctor);
            mProject.setLeader(projectLeader);
            mProject.setProjectname(projectname);

        } else {
            mProject = projects.get(0);
            mProject.setCollector(projectCollefctor);
            mProject.setLeader(projectLeader);
            mProject.setProjectname(projectname);
        }


        if (intentGeoinfoData != null && mIntentManagerProjectData != null) {

            //判断点号是否已经存在
            List<GeoInfo> geoifIds = DataSupport.select("code").where("code = ?", code).find(GeoInfo.class);
            if (!geoifIds.isEmpty()) {
                showToast("点号" + code + "已存在,请更改点号");
                return;
            }

            //地理信息
            GeoInfo geoInfo = new GeoInfo();
            geoInfo.setLatitude(latitude);
            geoInfo.setAddress(location);
            geoInfo.setLongtitude(longtitude);
            geoInfo.setDate(date);
            geoInfo.setCode(code);
            geoInfo.setElevation(elevation);
            geoInfo.setNote(note);
            //图片信息 selectedPhotos
            geoInfo.getPics().addAll(selectedPhotos);
            geoInfo.saveThrows();
            geoInfo.update(intentGeoinfoData.getId());
            if (geoInfo.isSaved()) {
                showToast("修改成功 ");
            } else {
                showToast("修改失败 ");
            }
        } else {

            //判断点号是否已经存在
            List<GeoInfo> geoifIds = DataSupport.select("code").where("code = ?", code).find(GeoInfo.class);
            if (!geoifIds.isEmpty()) {
                showToast("点号" + code + "已存在,请更改点号");
                return;
            }

            //地理信息
            GeoInfo geoInfo = new GeoInfo();
            geoInfo.setLatitude(latitude);
            geoInfo.setAddress(location);
            geoInfo.setLongtitude(longtitude);
            geoInfo.setDate(date);
            geoInfo.setCode(code);
            geoInfo.setElevation(elevation);
            geoInfo.setNote(note);
            //图片信息 selectedPhotos
            geoInfo.getPics().addAll(selectedPhotos);
            geoInfo.save();
            mProject.getGeoinfos().add(geoInfo);
            mProject.saveThrows();
            if (mProject.isSaved() && geoInfo.isSaved()) {
                showToast("保存成功 ");
            } else {
                showToast("保存失败 ");
            }
        }


    }

    /**
     * 初始化回显信息
     * 初始化回显信息
     */
    private void initProjectEditext() {

        if (mProjectData != null && !mProjectData.isEmpty()) {
            mTvProjectName.setText(mProjectData.get(mProjectData.size() - 1).getProjectname());
            mTvLeader.setText(mProjectData.get(mProjectData.size() - 1).getLeader());
            mTvCollector.setText(mProjectData.get(mProjectData.size() - 1).getCollector());
        } else {
            mTvProjectName.setText("");
            mTvLeader.setText("");
            mTvCollector.setText("");
        }
        if (mIntentManagerProjectData != null) {
            mTvProjectName.setText(mIntentManagerProjectData.getProjectname());
            mTvProjectName.setEnabled(false);
            mTvLeader.setText(mIntentManagerProjectData.getLeader());
            mTvLeader.setEnabled(false);
            mTvCollector.setText(mIntentManagerProjectData.getCollector());
            mTvCollector.setEnabled(false);
        }
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

    private String getFormattime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

}

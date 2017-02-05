package com.example.overdo.geoinfocollecter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.overdo.geoinfocollecter.activities.AboutActivity;
import com.example.overdo.geoinfocollecter.activities.BaseActivity;
import com.example.overdo.geoinfocollecter.activities.DriveRouteActivity;
import com.example.overdo.geoinfocollecter.activities.PointDetailActivity;
import com.example.overdo.geoinfocollecter.activities.ProjectManagerActivity;
import com.example.overdo.geoinfocollecter.db.GeoInfo;
import com.example.overdo.geoinfocollecter.db.Project;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * create by Overdo in 2017/01/23
 *
 */
public class MainActivity extends BaseActivity implements LocationSource, AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener {


    @InjectView(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorlayout;
    @InjectView(R.id.tv_location_title)
    TextView mTvLocationTitle;
    @InjectView(R.id.tv_location_detail)
    TextView mTvLocationDetail;
    @InjectView(R.id.action_go_there)
    ImageButton mActionGoThere;
    @InjectView(R.id.root)
    RelativeLayout mRoot;
    @InjectView(R.id.btn_collect)
    Button mBtnCollect;
    private AMap mMap;
    private MapView mMapView;
    private UiSettings mUiSetting;
    private Context mContext;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;
    private boolean isFirstIn = true;

    private static final String TAG = "MainActivity";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;

    private GeocodeSearch geocoderSearch;
    private String addressName;
    private LatLonPoint mCurrentPoint;
    private DPoint aimPoint;
    private String mCurrentElevation;
    private long exitTime = 0;

    private CharSequence[] maptypes = {"普通地图", "卫星地图", "交通地图"};
    private boolean[] checkedItems = {true, false, false};
    private List<Project> mProjectList;
    private DPoint mDesLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        initToolbarAndNavigationView();
        initMap();
        initAnimation();
        initMapClickListener();

        SQLiteDatabase db = LitePal.getDatabase();
    }

    private void initToolbarAndNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("地理信息采集助手");
    }


    /**
     * 底图属性初始化
     */
    private void initMap() {

        mMap = mMapView.getMap();
        mContext = this;
        mMap.setTrafficEnabled(false);
        mUiSetting = mMap.getUiSettings();
        mUiSetting.setCompassEnabled(true);
        mUiSetting.setZoomControlsEnabled(false);
        mUiSetting.setScaleControlsEnabled(true);
        mUiSetting.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        mUiSetting.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setLocationSource(this);// 设置定位监听
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        switch (getMapTypeonfig("maptype")) {
            case 0:
                mMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mMap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
        }

        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mMap.setMyLocationStyle(myLocationStyle);


        //初始化marker点击回调
        mMap.setOnMarkerClickListener(this);


        //初始化地理编码/逆地理编码
        if (geocoderSearch == null) {
            geocoderSearch = new GeocodeSearch(this);
        }
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 初始化地图点击事件
     */
    private void initMapClickListener() {

        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mCoordinatorlayout.getVisibility() == View.VISIBLE) {
                    mCoordinatorlayout.startAnimation(mHiddenAction);
                    mCoordinatorlayout.setVisibility(View.GONE);
                } else {
                    mCoordinatorlayout.startAnimation(mShowAction);
                    mCoordinatorlayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //添加marker
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(latLng)
                        .draggable(true));

                mTvLocationTitle.setText("获取详细地址中");
                mTvLocationDetail.setText(latLng.toString());

                if (mCoordinatorlayout.getVisibility() == View.GONE) {
                    mCoordinatorlayout.startAnimation(mShowAction);
                    mCoordinatorlayout.setVisibility(View.VISIBLE);
                }


                //==========坐标系转换成wgs84===================================
                CoordinateConverter converter = new CoordinateConverter(mContext);
                // CoordType.GPS 待转换坐标类型
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng待转换坐标点 DPoint类型
                aimPoint = new DPoint(latLng.latitude, latLng.longitude);
                try {
                    converter.coord(aimPoint);
                    // 执行转换操作
                    mDesLatLng = converter.convert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //===============================================

                //本app代码冗余逻辑混乱，只为完成毕业设计


                getAddress(aimPoint);
            }
        });
    }


    /**
     * 逆地理编码
     */
    public void getAddress(final DPoint dPoint) {

        RegeocodeQuery query = new RegeocodeQuery(
                new LatLonPoint(dPoint.getLatitude(), dPoint.getLongitude()), 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }


    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            Log.d(TAG, "onRegeocodeSearched: " + regeocodeResult.getRegeocodeAddress().getTowncode());

            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                mTvLocationTitle.setText(addressName);
                Log.d(TAG, "onRegeocodeSearched: " + addressName);
            } else {
                mTvLocationTitle.setText("无法获取到详细地名");

            }
        } else {

            mTvLocationTitle.setText("无法获取到详细地名");
        }
    }

    /**
     * 地理编码回掉
     *
     * @param geocodeResult
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
    }


    /**
     * 初始化进出场动画
     */
    private void initAnimation() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        mHiddenAction.setDuration(500);

    }


    /**
     * 定位信息回调函数
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                mCurrentPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mCurrentElevation = String.valueOf(aMapLocation.getAltitude());

                //第一次进入定位
                if (isFirstIn) {
                    isFirstIn = false;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                }


            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;

        //没有权限，申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        locateToCenter();
    }

    private void locateToCenter() {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        //销毁locationClient
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * marcker点击事件监听回调
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            if (mCoordinatorlayout.getVisibility() == View.GONE) {
                mCoordinatorlayout.startAnimation(mShowAction);
                mCoordinatorlayout.setVisibility(View.VISIBLE);
            }

            //目标点
            aimPoint = new DPoint(marker.getPosition().latitude, marker.getPosition().longitude);

//=============================================
            CoordinateConverter converter = new CoordinateConverter(this);
            // CoordType.GPS 待转换坐标类型
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标点 DPoint类型
            DPoint sourceLatLng = new DPoint(aimPoint.getLatitude(), aimPoint.getLongitude());
            try {
                converter.coord(sourceLatLng);
                // 执行转换操作
                mDesLatLng = converter.convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
//===============================================

            getAddress(aimPoint);

            String lat = mDesLatLng.getLatitude() + "";
            String lng = mDesLatLng.getLongitude() + "";

            mTvLocationDetail.setText("lat/lng：(" + lat.substring(0, 9) + "," + lng.substring(0, 9));
        }

        return true;
    }


    @OnClick({R.id.action_go_there, R.id.btn_collect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_go_there:
                Intent intent1 = new Intent(MainActivity.this, DriveRouteActivity.class);
                intent1.putExtra("start_point", mCurrentPoint);
                intent1.putExtra("aim_point", new LatLonPoint(aimPoint.getLatitude(), aimPoint.getLongitude()));
                if (aimPoint != null) {
                    startActivity(intent1);
                } else {
                    showToast("长按选定目标点");
                }
                break;
            case R.id.btn_collect:
                if (aimPoint != null) {

                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateNowStr = sdf.format(d);


                    GeoInfo geoinfo = new GeoInfo();
                    geoinfo.setDate(dateNowStr);
                    geoinfo.setAddress(addressName);
                    geoinfo.setLatitude(aimPoint.getLatitude() + "");
                    geoinfo.setLongtitude(aimPoint.getLongitude() + "");
                    geoinfo.setElevation(mCurrentElevation);

                    Intent intent2 = new Intent(MainActivity.this, PointDetailActivity.class);
                    intent2.putExtra("geo_info", geoinfo);
                    startActivity(intent2);
                } else {
                    showToast("长按选定采集点");
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collect:
                intentToClass(PointDetailActivity.class);
                break;
            case R.id.manager:
                intentToClass(ProjectManagerActivity.class);
                break;
            case R.id.distribute:
                dialogChooseShowWhitchProject();
                break;
            case R.id.layer:
                dialogChooseLayer();
                break;
            case R.id.about:
                intentToClass(AboutActivity.class);
                break;
        }
        return true;
    }

    private void dialogChooseShowWhitchProject() {

        mProjectList = DataSupport.findAll(Project.class);
        ArrayList<String> mList = new ArrayList<>();

        for (int i = 0; i < mProjectList.size(); i++) {
            mList.add(mProjectList.get(i).getProjectname().toString());
            setSPConfig(mProjectList.get(i).getProjectname(), "hide");
        }
        CharSequence[] mlist = mList.toArray(new CharSequence[mList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("查看项目点分布");

        builder.setMultiChoiceItems(mlist, new boolean[mlist.length]
                , new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            setSPConfig(mProjectList.get(which).getProjectname(), "show");
                        } else {
                            setSPConfig(mProjectList.get(which).getProjectname(), "hide");

                        }
                    }
                });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whitch) {
                dialog.dismiss();
                //获取配置信息，添加marker
                List<GeoInfo> list = getNeedAddMarKerProject();

                for (int i = 0; i < list.size(); i++) {

                    Log.d(TAG, "onClick: " + list.toString() + list.size());
                    //添加marker
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(new LatLng(Double.valueOf(list.get(i).getLatitude()),
                                    Double.valueOf((list.get(i).getLongtitude())))
                            )).setTitle(list.get(i).getCode());

                }
            }
        });

        builder.setNegativeButton("清空地图标记", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMap.clear();
            }
        });

        builder.show();
    }

    /**
     * 获取需要添加marker的project
     *
     * @return
     */
    private List getNeedAddMarKerProject() {
        List<Project> nlist = new ArrayList<Project>();
        for (int i = 0; i < mProjectList.size(); i++) {
            if (getSPConfig(mProjectList.get(i).getProjectname()).equals("show")) {
                nlist.add(mProjectList.get(i));
            }
        }

        List<GeoInfo> glist = new ArrayList<GeoInfo>();
        for (int i = 0; i < nlist.size(); i++) {

            final List<GeoInfo> mlist = DataSupport.where("project_id = ?", nlist.get(i).getId() + "").find(GeoInfo.class);
            glist.addAll(mlist);
        }

        return glist;
    }


    private void dialogChooseLayer() {
        int itemNum = getMapTypeonfig("maptype");
        Log.d(TAG, "dialogChooseLayer: " + itemNum);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择显示图层");
        builder.setSingleChoiceItems(maptypes, itemNum, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        setMapTypeConfig("maptype", 0);
                        break;
                    case 1:
                        mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        setMapTypeConfig("maptype", 1);
                        break;
                    case 2:
                        mMap.setMapType(AMap.MAP_TYPE_NAVI);
                        setMapTypeConfig("maptype", 2);
                        break;
                }
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void intentToClass(Class object) {
        startActivity(new Intent(MainActivity.this, object));
    }


    /**
     * 再按一次退出功能
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("权限定位被拒绝，到权限设置界面设置");

                } else {
                    locateToCenter();
                }
                break;
        }
    }
}

package com.example.overdo.geoinfocollecter.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.example.overdo.geoinfocollecter.App;

import static com.example.overdo.geoinfocollecter.entities.Constants.NORMAL_MAP;
import static com.example.overdo.geoinfocollecter.entities.Constants.SATELITE_MAP;
import static com.example.overdo.geoinfocollecter.entities.Constants.TRAFFIC_MAP;

/**
 * Created by Overdo on 2017/1/23.
 */

public class BaseActivity extends AppCompatActivity {

    public static final int PERMISSION_GRANTED = 1;
    private SharedPreferences mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有权限，申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_GRANTED);
        }

    }

    //displaying toast
    public void showToast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 权限申请回调函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("权限被拒绝");
                }
                break;
        }
    }


    public void saveConfig(String name, String maptype) {
        mConfig = App.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mConfig.edit();
        editor.putString(name, maptype);
        editor.commit();
    }

    public int getMapModeConfig(String configType) {
        if (mConfig == null) {
            mConfig = App.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        switch (mConfig.getString(configType, NORMAL_MAP)) {
            case NORMAL_MAP:
                return AMap.MAP_TYPE_NORMAL;
            case SATELITE_MAP:
                return AMap.MAP_TYPE_SATELLITE;
            case TRAFFIC_MAP:
                return AMap.MAP_TYPE_NAVI;

        }
        return AMap.MAP_TYPE_NORMAL;
    }

    public String getProjectConfig(String configType) {
        if (mConfig == null) {
            mConfig = App.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        return mConfig.getString(configType,"");
    }
}

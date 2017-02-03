package com.example.overdo.geoinfocollecter.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Overdo on 2017/1/23.
 */

public class BaseActivity extends AppCompatActivity {

    public static final int PERMISSION_GRANTED = 1;
    private Toast mToast;
    private SharedPreferences mSp;

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
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT);
        mToast.show();
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

    public void setSPConfig(String type, String value) {
        //获取到sharepreference 对象， 参数一为xml文件名，参数为文件的可操作模式
        if (mSp == null) {
            mSp = this.getSharedPreferences("config", MODE_APPEND);
        }
        //获取到编辑对象
        SharedPreferences.Editor edit = mSp.edit();
        //添加新的值，可见是键值对的形式添加
        edit.putString(type, value);
        //提交.
        edit.commit();
    }

    public String getSPConfig(String type) {
        //获取到sharepreference 对象， 参数一为xml文件名，参数为文件的可操作模式
        if (mSp == null) {
            mSp = this.getSharedPreferences("config", MODE_APPEND);
        }
        return mSp.getString(type, "hide");
    }

    public void setMapTypeConfig(String type, int value) {
        //获取到sharepreference 对象， 参数一为xml文件名，参数为文件的可操作模式
        if (mSp == null) {
            mSp = this.getSharedPreferences("config", MODE_APPEND);
        }
        //获取到编辑对象
        SharedPreferences.Editor edit = mSp.edit();
        //添加新的值，可见是键值对的形式添加
        edit.putInt(type, value);
        //提交.
        edit.commit();
    }

    public int getMapTypeonfig(String type) {
        //获取到sharepreference 对象， 参数一为xml文件名，参数为文件的可操作模式
        if (mSp == null) {
            mSp = this.getSharedPreferences("config", MODE_APPEND);
        }
        return mSp.getInt(type,0);
    }


}

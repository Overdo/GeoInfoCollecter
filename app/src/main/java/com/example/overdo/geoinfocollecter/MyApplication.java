package com.example.overdo.geoinfocollecter;

import android.app.Application;
import android.content.Context;

/**
 * Created by Overdo on 2017/1/23.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }


}

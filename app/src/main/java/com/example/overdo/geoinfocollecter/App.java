package com.example.overdo.geoinfocollecter;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import org.litepal.LitePal;

/**
 * Created by Overdo on 2017/1/23.
 */

public class App extends Application {

    private static Context context;
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;


    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}

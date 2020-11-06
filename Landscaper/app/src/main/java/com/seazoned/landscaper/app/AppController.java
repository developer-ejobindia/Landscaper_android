package com.seazoned.landscaper.app;

import android.app.Application;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by root on 4/4/18.
 */

public class AppController extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}

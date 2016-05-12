package com.example.zhaoh.retrofit2_text;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by zhaoh on 2016/4/18.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}

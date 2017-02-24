package com.netease.frescodemo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by MrDong on 2017/2/24.
 */

public class FrescoApplication extends Application {

    static FrescoApplication frescoApplacation;


    public static FrescoApplication getInstance() {
        return frescoApplacation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        frescoApplacation = this;
        Fresco.initialize(this);
    }
}

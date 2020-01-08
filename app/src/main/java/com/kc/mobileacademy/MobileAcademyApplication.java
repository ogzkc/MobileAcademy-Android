package com.kc.mobileacademy;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by Oguz on 26.02.2016.
 */
public class MobileAcademyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(this);
    }
}

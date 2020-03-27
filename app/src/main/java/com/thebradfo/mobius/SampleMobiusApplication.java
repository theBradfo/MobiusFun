package com.thebradfo.mobius;

import android.app.Application;

import timber.log.Timber;

public class SampleMobiusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}

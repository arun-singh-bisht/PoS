package com.posfone.promote.posfone;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class PoSFoneApplicationClass extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }

}

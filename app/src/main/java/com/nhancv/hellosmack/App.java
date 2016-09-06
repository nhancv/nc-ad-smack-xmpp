package com.nhancv.hellosmack;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class App extends Application {
    public static Bus bus = new Bus();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

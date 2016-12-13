package com.nhancv.hellosmack;

import android.app.Application;

import com.nhancv.npreferences.NPreferences;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class App extends Application {
    public static final EventBus bus = new EventBus();
    public static final String ENCRYPT_KEY = "F/*-7lk(*(&#KD(S(()";
    @Override
    public void onCreate() {
        super.onCreate();
        new NPreferences.Builder(this).withEncryptionPassword(ENCRYPT_KEY).build();
    }
}

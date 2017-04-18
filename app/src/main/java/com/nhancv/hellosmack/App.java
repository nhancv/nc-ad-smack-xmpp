package com.nhancv.hellosmack;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nhancv.hellosmack.helper.DefaultXmppConfig;
import com.nhancv.npreferences.NPreferences;
import com.nhancv.xmpp.XmppPresenter;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class App extends MultiDexApplication {
    public static final String ENCRYPT_KEY = "F/*-7lk(*(&#KD(S(()";

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new NPreferences.Builder(this).withEncryptionPassword(ENCRYPT_KEY).build();
        //Setup config XmppPresenter
        XmppPresenter.setXmppConfig(new DefaultXmppConfig());
    }
}

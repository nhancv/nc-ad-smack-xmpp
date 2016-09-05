package com.nhancv.hellosmack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by nhancao on 9/5/16.
 */
public class XmppService extends Service {

    private String userName;
    private String passWord;
    private XmppHandler xmpp = new XmppHandler();

    public XmppService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            userName = intent.getStringExtra("user");
            passWord = intent.getStringExtra("pwd");
            xmpp.init(userName, passWord);
            xmpp.connectConnection();
        }

        return 0;
    }

    @Override
    public void onDestroy() {
        xmpp.disconnectConnection();
        super.onDestroy();
    }

}

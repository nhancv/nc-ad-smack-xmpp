package com.nhancv.hellosmack.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.nhancv.hellosmack.bus.BaseBus;
import com.nhancv.hellosmack.bus.NetBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by nhancao on 2/15/17.
 */

public class NConnectivityReceiver extends BroadcastReceiver {
    private static final String TAG = NConnectivityReceiver.class.getSimpleName();

    private static EventBus bus = new EventBus();

    public static EventBus getBus() {
        return bus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                if (NNetworkUtils.isConnected(context)) {
                    //@nhancv TODO: send broadcast message
                    postEvent(new NetBus(NConnectivityReceiver.class, NetBus.Type.CONNECTED));
                } else {
                    postEvent(new NetBus(NConnectivityReceiver.class, NetBus.Type.NO_CONNECTION));
                }
                break;
        }
    }

    <O extends BaseBus> void postEvent(O object) {
        getBus().post(object);
    }

}
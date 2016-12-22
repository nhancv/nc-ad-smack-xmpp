package com.nhancv.xmpp.listener;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by nhancao on 12/21/16.
 */

public abstract class ErrorXmppConListener implements ConnectionListener {
    private static final String TAG = ErrorXmppConListener.class.getSimpleName();

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG, "connected");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "authenticated");
    }
}

package com.nhancv.xmpp.listener;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by nhancao on 12/13/16.
 */

public abstract class AuthXmppConListener implements ConnectionListener {

    private static final String TAG = AuthXmppConListener.class.getSimpleName();

    @Override
    public void connectionClosed() {
        Log.d(TAG, "Connection Closed!");
    }

    @Override
    public void connectionClosedOnError(final Exception e) {
        Log.e(TAG, "connectionClosedOnError: " + e.getMessage());
    }

    @Override
    public void reconnectingIn(int arg0) {
        Log.d(TAG, "Reconnecting... " + arg0);
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        Log.e(TAG, "Reconnection Failed!");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(TAG, "Reconnection Successful");
    }

}

package com.nhancv.xmpp;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by nhancao on 12/13/16.
 */

public abstract class AbstractXmppConListener implements ConnectionListener {
    @Override
    public void connectionClosed() {
        Log.d("xmpp", "Connection Closed!");
    }

    @Override
    public void connectionClosedOnError(final Exception e) {
        Log.d("xmpp", "ConnectionClosedOn Error!");
    }

    @Override
    public void reconnectingIn(int arg0) {
        Log.d("xmpp", "Reconnecting... " + arg0);
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        Log.d("xmpp", "ReconnectionFailed!");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d("xmpp", "ReconnectionSuccessful");
    }

}

package com.nhancv.xmpp;

import org.jivesoftware.smack.SmackException;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppListener {

    public interface IXmppConnListener {
        void success();

        void error(Exception ex);
    }

    public interface IXmppLoginListener {

        void loginSuccess();

        void loginError(Exception ex);
    }

    public interface IXmppCreateListener {
        void createSuccess() throws SmackException.NotConnectedException;

        void createError(Exception ex);
    }

    public interface IXmppCallback<T> {
        void callback(T item);
    }


}

package com.nhancv.hellosmack.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by nhancao on 12/13/16.
 */

public interface IXmppPresenter {

    IXmppConnector getXmppConnector();

    void login(String username, String passwod, XmppListener.IXmppLoginListener loginConnectionListener)
            throws XMPPException, IOException, SmackException;

    void createUser(String username, String password, XmppListener.IXmppCreateListener createConnectionListener) throws XMPPException, IOException, SmackException;
}

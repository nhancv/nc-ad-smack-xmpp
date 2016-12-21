package com.nhancv.xmpp;

import android.support.annotation.NonNull;

import com.nhancv.xmpp.listener.XmppListener;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by nhancao on 12/13/16.
 */

public interface IXmppConnector {

    void setupLoginConnection(@NonNull IXmppCredential _xmppCredential, @NonNull XmppListener.IXmppLoginListener _loginConnectionListener);

    void setupCreateConnection(@NonNull XmppListener.IXmppConnListener _createConnectionListener);

    void createConnection() throws IOException, XMPPException, SmackException;

    void terminalConnection() throws SmackException.NotConnectedException;

    AbstractXMPPConnection getConnection();

    IXmppCredential getXmppCredential();

    IXmppConfig getXmppConfig();

    boolean isCredentialSetup();

}

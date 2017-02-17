package com.nhancv.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nhancv.xmpp.listener.AuthXmppConListener;
import com.nhancv.xmpp.listener.XmppListener;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppConnector implements IXmppConnector {
    private static final String TAG = XmppConnector.class.getSimpleName();

    private IXmppConfig xmppConfig;
    private IXmppCredential xmppCredential;
    private XmppListener.IXmppLoginListener loginConnectionListener;
    private XmppListener.IXmppConnListener createConnectionListener;

    private AbstractXMPPConnection connection;
    private AuthXmppConListener connectionListener;

    public XmppConnector() {
        this(new XmppConfig());
    }

    public XmppConnector(IXmppConfig xmppConfig) {
        if (xmppConfig == null) xmppConfig = new XmppConfig();
        this.xmppConfig = xmppConfig;
        this.connectionListener = new XmppConnectionListener();
    }

    @Override
    public void setupLoginConnection(@NonNull IXmppCredential _xmppCredential, @NonNull XmppListener.IXmppLoginListener _loginConnectionListener) {
        this.xmppCredential = _xmppCredential;
        this.loginConnectionListener = _loginConnectionListener;

        setupXmppTcpConnection();
    }

    @Override
    public void setupCreateConnection(@NonNull XmppListener.IXmppConnListener _createConnectionListener) {
        this.xmppCredential = null;
        this.createConnectionListener = _createConnectionListener;

        setupXmppTcpConnection();
    }

    /**
     * If you want to implement http://xmpp.org/extensions/xep-0013.html (Flexible Offline Message Retrieval)
     * You have to set OfflineMessageManager before init presence available
     * https://docs.ejabberd.im/admin/guide/xep/#toc_12
     * <p>
     * In smack to do this: set send presence flag to false in config
     * configBuilder.setSendPresence(false);
     * set offline message manager and get message count
     * send stanza presence available (sendStanza(new Presence(Presence.Type.available));)
     */
    private void setupXmppTcpConnection() {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        if (xmppCredential != null) {
            configBuilder.setUsernameAndPassword(xmppCredential.getUsername(), xmppCredential.getPassword());
        }
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setResource("");
        configBuilder.setServiceName(xmppConfig.getDomain());
        configBuilder.setHost(xmppConfig.getHost());
        configBuilder.setPort(xmppConfig.getPort());

        configBuilder.setDebuggerEnabled(false);
//        For offline message
//        configBuilder.setSendPresence(false);

        connection = new XMPPTCPConnection(configBuilder.build());
        connection.setPacketReplyTimeout(5000);
        connection.addConnectionListener(connectionListener);

    }

    @Override
    public void createConnection() throws IOException, XMPPException, SmackException {
        connection.connect();
    }

    @Override
    public void terminalConnection() throws SmackException.NotConnectedException {
        connection.disconnect(new Presence(Presence.Type.unavailable));
    }

    @Override
    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    @Override
    public IXmppCredential getXmppCredential() {
        return xmppCredential;
    }

    @Override
    public IXmppConfig getXmppConfig() {
        return xmppConfig;
    }

    @Override
    public boolean isCredentialSetup() {
        return xmppCredential != null;
    }

    public class XmppConnectionListener extends AuthXmppConListener {
        @Override
        public void connected(final XMPPConnection connection) {
            if (!isCredentialSetup()) {
                createConnectionListener.success();
            } else if (!connection.isAuthenticated()) {
                try {
                    getConnection().login(getXmppCredential().getUsername(), getXmppCredential().getPassword());
                } catch (XMPPException | SmackException | IOException e) {
                    loginConnectionListener.loginError(e);
                }
            }
            Log.d(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            if (isCredentialSetup()) {
                loginConnectionListener.loginSuccess();
            }
            Log.d(TAG, "Authenticated!");
        }

    }


}

package com.nhancv.hellosmack.xmpp;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppPresenter implements IXmppPresenter {
    private static final String TAG = XmppPresenter.class.getSimpleName();

    private static XmppPresenter instance = new XmppPresenter();
    private IXmppConnector xmppConnector;

    public static XmppPresenter getInstance() {
        return instance;
    }

    @Override
    public IXmppConnector getXmppConnector() {
        return xmppConnector;
    }

    @Override
    public void login(String username, String passwod, XmppListener.IXmppLoginListener loginConnectionListener)
            throws XMPPException, IOException, SmackException {
        IXmppCredential xmppCredential = new XmppCredential(username, passwod);
        xmppConnector = new XmppConnector();
        xmppConnector.setupLoginConnection(xmppCredential, loginConnectionListener);
        xmppConnector.createConnection();
    }

    @Override
    public void createUser(String username, String password, XmppListener.IXmppCreateListener createConnectionListener)
            throws XMPPException, IOException, SmackException {
        xmppConnector = new XmppConnector();
        xmppConnector.setupCreateConnection(new XmppListener.IXmppConnListener() {
            @Override
            public void success() {
                AccountManager accountManager = AccountManager.getInstance(xmppConnector.getConnection());
                accountManager.sensitiveOperationOverInsecureConnection(true);
                try {
                    if (accountManager.supportsAccountCreation()) {
                        accountManager.createAccount(username, password);
                        createConnectionListener.createSuccess();
                    } else {
                        Log.e(TAG, "success: Server not support create account");
                        createConnectionListener.createError(new Exception("Server not support create account"));
                    }
                } catch (XMPPException.XMPPErrorException e) {
                    if (e.getXMPPError().getType() == XMPPError.Type.CANCEL) {
                        createConnectionListener.createError(new Exception("User exists"));
                    } else {
                        createConnectionListener.createError(e);
                    }
                } catch (SmackException.NoResponseException | SmackException.NotConnectedException e) {
                    createConnectionListener.createError(e);
                }
            }

            @Override
            public void error(Exception ex) {
                createConnectionListener.createError(ex);
            }
        });
        xmppConnector.createConnection();
    }


}

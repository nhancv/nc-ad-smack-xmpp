package com.nhancv.hellosmack;

import android.util.Log;

import com.nhancv.hellosmack.bus.LoginBus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by nhancao on 9/5/16.
 */
public class XmppHandler {
    private static final String TAG = XmppHandler.class.getSimpleName();
    private static final String DOMAIN = "192.168.1.59";
    private static final int PORT = 9090;
    private static XmppHandler instance = new XmppHandler();
    AbstractXMPPConnection connection;
    ChatManager chatmanager;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    private String userName = "";
    private String passWord = "";

    public static XmppHandler getInstance() {
        return instance;
    }

    /**
     * Initialize xmpp config
     *
     * @param userId
     * @param pwd
     * @return
     */
    public XmppHandler init(String userId, String pwd) {
        Log.i("XMPP", "Initializing!");
        this.userName = userId;
        this.passWord = pwd;
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(userName, passWord);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setPort(PORT);
        configBuilder.setDebuggerEnabled(false);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.addConnectionListener(connectionListener);
        return this;
    }

    /**
     * Terminal connection
     */
    public void terminalConnection() {
        Utils.aSyncTask(subscriber -> {
            connection.disconnect();
        });
    }

    /**
     * Create connection
     */
    public void createConnection() {
        Utils.aSyncTask(subscriber -> {
            try {
                connection.setPacketReplyTimeout(10000);
                connection.connect();
            } catch (SmackException | XMPPException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Login
     */
    public void login() {
        try {
            connection.login(userName, passWord);
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Utils.runOnUi(() -> {
                App.bus.post(new LoginBus(XmppHandler.class, LoginBus.ERROR, e.getMessage()));
            });
        }
    }

    /**
     * Send Msg
     */
    public void sendMsg() {
        if (connection.isConnected()) {
            // Assume we've created an XMPPConnection name "connection"._
            chatmanager = ChatManager.getInstanceFor(connection);
            newChat = chatmanager.createChat("admin@192.168.1.59");
            try {
                newChat.sendMessage("Howdy!");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------FUNCTION: CHAT HANDLING---------------------------//

    public void getUserList() {
        Utils.aSyncTask(subscriber -> {
            Roster roster = Roster.getInstanceFor(connection);
            Collection<RosterEntry> entries = roster.getEntries();
            Presence presence;

            for (RosterEntry entry : entries) {
                presence = roster.getPresence(entry.getUser());
                Log.e(TAG, "getUserList: " + entry.getUser());
                Log.e(TAG, "getUserList: " + presence.getType().name());
                Log.e(TAG, "getUserList: " + presence.getStatus());
            }

            sendMsg();
        });
    }

    /**
     * Connection Listener to check connection state
     */
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            if (!connection.isAuthenticated()) {
                login();
            }
            Log.d("xmpp", "Connected!");
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp", "ConnectionCLosed!");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Utils.runOnUi(() -> {
                App.bus.post(new LoginBus(XmppHandler.class, LoginBus.ERROR, e.getMessage()));
            });
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

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            Utils.runOnUi(() -> {
                App.bus.post(new LoginBus(XmppHandler.class, LoginBus.SUCCESS));
            });
        }
    }
}

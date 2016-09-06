package com.nhancv.hellosmack;

import android.os.AsyncTask;
import android.util.Log;

import com.nhancv.hellosmack.bus.LoginBus;
import com.nhancv.hellosmack.listener.CallbackListener;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by nhancao on 9/5/16.
 */
public class XmppHandler {

    private static final String DOMAIN = "192.168.1.59";
    private static final int PORT = 9090;
    private static XmppHandler instance = new XmppHandler();
    AbstractXMPPConnection connection;
    ChatManager chatmanager;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    private String userName = "";
    private String passWord = "";
    private boolean connected;
    private boolean chat_created;
    private boolean loggedin;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    /**
     * Create connection
     */
    public void createConnection() {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... arg0) {
                // Create a connection
                try {
                    connection.setPacketReplyTimeout(10000);
                    connection.connect();
                    connected = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        connectionThread.execute();
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

    /**
     * Login
     */
    public void login() {
        try {
            connection.login(userName, passWord);
            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }
    }

    /**
     * Connection Listener to check connection state
     */
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
            Utils.postToUi(new CallbackListener() {
                @Override
                public void callback() {
                    App.bus.post(new LoginBus(XmppHandler.class, LoginBus.SUCCESS));
                }
            });
            Log.d("xmpp", "Connected!");
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(final Exception arg0) {
            Utils.postToUi(new CallbackListener() {
                @Override
                public void callback() {
                    App.bus.post(new LoginBus(XmppHandler.class, LoginBus.ERROR, arg0.getCause()));
                }
            });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d("xmpp", "Reconnecting... " + arg0);
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;
            chat_created = false;
        }
    }


}

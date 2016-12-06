package com.nhancv.hellosmack;

import android.util.Log;

import com.nhancv.hellosmack.bus.LoginBus;
import com.nhancv.hellosmack.helper.Utils;
import com.nhancv.hellosmack.listener.ICollections;
import com.nhancv.hellosmack.listener.XMPPStanzaListener;
import com.nhancv.hellosmack.model.User;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by nhancao on 9/5/16.
 */
public class XmppHandler {
    private static final String TAG = XmppHandler.class.getSimpleName();
    private static final String HOST = "local.beesightsoft.com";
    private static final String DOMAIN = "local.beesightsoft.com";
    private static final int PORT = 7008;
    private static XmppHandler instance = new XmppHandler();

    AbstractXMPPConnection connection;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    private List<User> userList = new ArrayList<>();
    private String userName = "";
    private String passWord = "";

    public static XmppHandler getInstance() {
        return instance;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
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
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setDebuggerEnabled(false);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.setPacketReplyTimeout(10000);
        connection.addConnectionListener(connectionListener);

        return this;
    }

    /**
     * Terminal connection
     */
    public void terminalConnection() {
        try {
            connection.disconnect(new Presence(Presence.Type.unavailable));
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create connection
     */
    public void createConnection() {
        Utils.aSyncTask(subscriber -> {
            try {
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
    //----------------------------FUNCTION: CHAT HANDLING---------------------------//

    /**
     * Create group chat
     *
     * @param room
     * @param ownerJid
     * @return
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException
     */
    public MultiUserChat createGroupChat(String room, String ownerJid) throws XMPPException.XMPPErrorException, SmackException {
        MultiUserChat chatRoom = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(ownerJid);
        chatRoom.create("test");
        Form form = chatRoom.getConfigurationForm().createAnswerForm();
        form.setAnswer("muc#roomconfig_publicroom", true);
        form.setAnswer("muc#roomconfig_roomname", "room786");
        form.setAnswer("muc#roomconfig_roomowners", "owners");
        form.setAnswer("muc#roomconfig_persistentroom", true);
        chatRoom.sendConfigurationForm(form);
        MultiUserChatManager.getInstanceFor(connection).addInvitationListener(new GroupChatListener());
        return chatRoom;
    }

    /**
     * Create connection without user and password support for create new account
     *
     * @param callbackListener
     */
    public void createConnectionWithoutCredentials(ICollections.ObjectCallBack<XMPPConnection> callbackListener) {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setResource("Android");
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setDebuggerEnabled(false);
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.setPacketReplyTimeout(10000);
        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                callbackListener.callback(connection);
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {

            }

            @Override
            public void connectionClosed() {

            }

            @Override
            public void connectionClosedOnError(Exception e) {

            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int seconds) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        });
        createConnection();
    }

    /**
     * Implement search by username
     *
     * @param user
     */
    public void searchUser(String user) {
        try {
            Log.e(TAG, "searchUser: ");
            UserSearchManager searchManager = new UserSearchManager(connection);
            Form searchForm = searchManager.getSearchForm("search." + connection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", user);
            ReportedData data = searchManager.getSearchResults(answerForm, "search." + connection.getServiceName());
            if (data.getRows() != null) {
                for (ReportedData.Row row : data.getRows()) {
                    for (String value : row.getValues("jid")) {
                        Log.e(TAG, "test: " + value);
                    }
                }
            }
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create new account
     *
     * @param username
     * @param password
     */
    public void createNewAccount(String username, String password, ICollections.CallingListener callingListener) {

        if (connection == null || !connection.isConnected()) {
            createConnectionWithoutCredentials(conn -> {
                createNewAccount(username, password, callingListener);
            });
        } else {
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            try {
                if (accountManager.supportsAccountCreation()) {
                    accountManager.createAccount(username, password);
                    callingListener.success();
                } else {
                    Log.e(TAG, "createNewAccount: not support create account");
                    callingListener.error("Server not support create account");
                }
            } catch (XMPPException.XMPPErrorException e) {
                if (e.getXMPPError().getType() == XMPPError.Type.CANCEL) {
                    callingListener.error("User exists");
                    Log.e(TAG, "createNewAccount: user exists");
                }
            } catch (SmackException.NoResponseException | SmackException.NotConnectedException e) {
                callingListener.error(e.getMessage());
            }
        }

    }

    /**
     * Setup listener for stanze packet
     *
     * @param packetListeners
     */
    public void setupListener(List<XMPPStanzaListener> packetListeners) {
        for (XMPPStanzaListener listener : packetListeners) {
            connection.addAsyncStanzaListener(listener.getStanzaListener(), new StanzaTypeFilter(listener.getPacketType()));
        }
    }

    /**
     * Setup chat session
     *
     * @param listener
     * @param userJid
     */
    public void createChatSession(StanzaListener listener, String userJid) {
        connection.addAsyncStanzaListener(listener, new AndFilter(new StanzaTypeFilter(Message.class), new FromMatchesFilter(userJid, false)));
    }

    /**
     * Terminal chat session
     *
     * @param listener
     */
    public void terminalChatSession(StanzaListener listener) {
        connection.removeAsyncStanzaListener(listener);
    }

    /**
     * Remove roster
     *
     * @param userJid
     */
    public void removeRoster(String userJid) {
        RosterPacket packet = new RosterPacket();
        packet.setType(IQ.Type.set);
        RosterPacket.Item item = new RosterPacket.Item(userJid, null);
        item.setItemType(RosterPacket.ItemType.remove);
        packet.addRosterItem(item);
        try {
            connection.sendStanza(packet);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request user
     *
     * @param userJid
     * @param type
     */
    public void requestUser(String userJid, Presence.Type type) {
        Presence subscribe = new Presence(type);
        subscribe.setTo(userJid);
        try {
            connection.sendStanza(subscribe);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get current user
     *
     * @return
     */
    public String getCurrentUser() {
        return connection.getUser();
    }

    /**
     * Setup for chat
     *
     * @param userJID: admin@192.168.1.59
     */
    public Chat setupChat(String userJID) {
        if (connection.isConnected()) {
            return ChatManager.getInstanceFor(connection).createChat(userJID);
        } else {
            return null;
        }
    }

    /**
     * Setup incoming chat
     *
     * @param chatObjectCallBack
     */
    public void setupIncomingChat(ICollections.ObjectCallBack<Chat> chatObjectCallBack) {
        if (connection.isConnected()) {
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener((chat, createdLocally) -> {
                if (!createdLocally) {
                    Utils.runOnUi(() -> {
                        chatObjectCallBack.callback(chat);
                    });
                }
            });
        }
    }

    /**
     * Get user list
     */
    public void getUserList(ICollections.ObjectCallBack<Roster> listItemsCallback) {
        Utils.aSyncTask(subscriber -> {
            userList = new ArrayList<>();
            Roster roster = Roster.getInstanceFor(connection);
            Collection<RosterEntry> entries = roster.getEntries();
            Presence presence;

            for (RosterEntry entry : entries) {
                presence = roster.getPresence(entry.getUser());
                userList.add(new User(entry.getUser(), presence));
            }
            subscriber.onNext(roster);
        }, new Action1<Roster>() {
            @Override
            public void call(Roster roster) {
                listItemsCallback.callback(roster);
            }
        });
    }

    /**
     * Parse address to userJid
     *
     * @param address: test2@192.168.1.59/DESKTOP-IUBFVPO
     * @return test2@192.168.1.59
     */
    public String parseUserJid(String address) {
        int index = address.indexOf("/");
        if (index != -1) {
            address = address.substring(0, index);
        }
        return address;
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

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            Utils.runOnUi(() -> {
                App.bus.post(new LoginBus(XmppHandler.class, LoginBus.SUCCESS));
            });
        }
    }

    public class GroupChatListener implements InvitationListener {
        @Override
        public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
            Log.e(TAG, "invitationReceived: Entered invitation handler... " + message);
            try {
                room.join(inviter);
                Log.e(TAG, "invitationReceived: accepted");
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

}

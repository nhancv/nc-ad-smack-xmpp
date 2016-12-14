package com.nhancv.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
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

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppPresenter implements IXmppPresenter {
    private static final String TAG = XmppPresenter.class.getSimpleName();

    private static XmppPresenter instance = new XmppPresenter();
    private List<BaseRoster> userList = new ArrayList<>();
    private IXmppConnector xmppConnector;

    public static XmppPresenter getInstance() {
        return instance;
    }

    @Override
    public IXmppConnector getXmppConnector() {
        return xmppConnector;
    }

    @Override
    public void login(String userJid, String passwod, XmppListener.IXmppLoginListener loginConnectionListener)
            throws IOException, SmackException, XMPPException {
        IXmppCredential xmppCredential = new XmppCredential(userJid, passwod);
        xmppConnector = new XmppConnector();
        xmppConnector.setupLoginConnection(xmppCredential, loginConnectionListener);
        xmppConnector.createConnection();
    }

    @Override
    public void createUser(String userJid, String password, XmppListener.IXmppCreateListener createConnectionListener)
            throws XMPPException, IOException, SmackException {
        xmppConnector = new XmppConnector();
        xmppConnector.setupCreateConnection(new XmppListener.IXmppConnListener() {
            @Override
            public void success() {
                AccountManager accountManager = AccountManager.getInstance(xmppConnector.getConnection());
                accountManager.sensitiveOperationOverInsecureConnection(true);
                try {
                    if (accountManager.supportsAccountCreation()) {
                        accountManager.createAccount(userJid, password);
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

    @Override
    public void sendStanza(@NonNull Stanza packet) throws SmackException.NotConnectedException {
        xmppConnector.getConnection().sendStanza(packet);
    }

    @Override
    public void sendInviteRequest(String userJid) throws SmackException.NotConnectedException {
        Presence subscribe = new Presence(Presence.Type.subscribe);
        subscribe.setTo(userJid);
        sendStanza(subscribe);
    }

    @Override
    public void acceptInviteRequest(String userJid) throws SmackException.NotConnectedException {
        Presence subscribe = new Presence(Presence.Type.subscribed);
        subscribe.setTo(userJid);
        sendStanza(subscribe);

        subscribe.setType(Presence.Type.subscribe);
        sendStanza(subscribe);
    }

    @Override
    public void sendUnFriendRequest(String userJid) throws SmackException.NotConnectedException {
        RosterPacket subscribe = new RosterPacket();
        subscribe.setType(IQ.Type.set);
        RosterPacket.Item item = new RosterPacket.Item(userJid, null);
        item.setItemType(RosterPacket.ItemType.remove);
        subscribe.addRosterItem(item);
        sendStanza(subscribe);
    }

    @Override
    public void acceptUnFriendRequest(String userJid) throws SmackException.NotConnectedException {
        Presence subscribe = new Presence(Presence.Type.unsubscribed);
        subscribe.setTo(userJid);
        sendStanza(subscribe);
    }

    @Override
    public void removeAsyncStanzaListener(StanzaListener listener) {
        xmppConnector.getConnection().removeAsyncStanzaListener(listener);
    }

    @Override
    public void addAsyncStanzaListener(StanzaPackageType packetListener) {
        addAsyncStanzaListener(packetListener.getStanzaListener(),
                new StanzaTypeFilter(packetListener.getPacketType()));
    }

    @Override
    public void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter) {
        xmppConnector.getConnection().addAsyncStanzaListener(packetListener, packetFilter);
    }

    @Override
    public void addListStanzaListener(List<StanzaPackageType> packetListeners) {
        for (StanzaPackageType listener : packetListeners) {
            addAsyncStanzaListener(listener.getStanzaListener(), new StanzaTypeFilter(listener.getPacketType()));
        }
    }

    @Override
    public void setAutoAcceptSubscribe() {
        StanzaPackageType autoAcceptSubscribe = new StanzaPackageType(packet -> {
            if (packet instanceof Presence) {
                Log.e(TAG, "Presence: " + packet);
                Presence presence = (Presence) packet;
                if (presence.getType() != null) {
                    switch (presence.getType()) {
                        case subscribe:
                            XmppPresenter.getInstance().acceptInviteRequest(presence.getFrom());
                            break;
                        case unsubscribe:
                            XmppPresenter.getInstance().acceptUnFriendRequest(presence.getFrom());
                            break;

                    }
                }
            }
        }, Stanza.class);
        addAsyncStanzaListener(autoAcceptSubscribe.getStanzaListener(),
                new StanzaTypeFilter(autoAcceptSubscribe.getPacketType()));
    }

    @Override
    public Chat preparingChat(String toJid) {
        if (xmppConnector.getConnection().isConnected()) {
            return ChatManager.getInstanceFor(xmppConnector.getConnection()).createChat(toJid);
        } else {
            return null;
        }
    }

    @Override
    public void openChatSession(StanzaListener listener, String toJid) {
        addAsyncStanzaListener(listener,
                new AndFilter(new StanzaTypeFilter(Message.class),
                        new FromMatchesFilter(toJid, false)));
    }

    @Override
    public void closeChatSession(StanzaListener listener) {
        removeAsyncStanzaListener(listener);
    }

    @Override
    public List<BaseRoster> getCurrentRosterList() {
        return userList;
    }

    @Override
    public Roster setupRosterList(RosterListener rosterListener) {
        userList = new ArrayList<>();
        Roster roster = Roster.getInstanceFor(xmppConnector.getConnection());
        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;

        for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getUser());
            userList.add(new BaseRoster(entry.getUser(), presence));
        }
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {
                for (String item : addresses) {
                    Presence presence = roster.getPresence(item);
                    XmppPresenter.getInstance().getCurrentRosterList().add(new BaseRoster(item, presence));
                }
                rosterListener.entriesAdded(addresses);
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                for (String item : addresses) {
                    for (BaseRoster user : XmppPresenter.getInstance().getCurrentRosterList()) {
                        if (item.contains(user.getName())) {
                            Presence presence = roster.getPresence(item);
                            user.setPresence(presence);
                            break;
                        }
                    }
                }
                rosterListener.entriesUpdated(addresses);
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                for (String item : addresses) {
                    for (int i = 0; i < XmppPresenter.getInstance().getCurrentRosterList().size(); i++) {
                        BaseRoster user = XmppPresenter.getInstance().getCurrentRosterList().get(i);
                        if (item.contains(user.getName())) {
                            XmppPresenter.getInstance().getCurrentRosterList().remove(i);
                            break;
                        }
                    }
                }
                rosterListener.entriesDeleted(addresses);
            }

            @Override
            public void presenceChanged(Presence presence) {
                for (BaseRoster user : XmppPresenter.getInstance().getCurrentRosterList()) {
                    if (presence.getFrom().contains(user.getName())) {
                        user.setPresence(presence);
                        break;
                    }
                }
                rosterListener.presenceChanged(presence);
            }
        });
        return roster;
    }

    @Override
    public String getCurrentUser() {
        return xmppConnector.getConnection().getUser();
    }


    //Other implement
    public void searchUser(String user) {
        try {
            Log.e(TAG, "searchUser: ");
            UserSearchManager searchManager = new UserSearchManager(xmppConnector.getConnection());
            Form searchForm = searchManager.getSearchForm("search." + xmppConnector.getConnection().getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", user);
            ReportedData data = searchManager.getSearchResults(answerForm, "search." + xmppConnector.getConnection().getServiceName());
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

    public void setupIncomingChat(XmppListener.IXmppCallback<Chat> chatObjectCallBack) {
        if (xmppConnector.getConnection().isConnected()) {
            ChatManager chatManager = ChatManager.getInstanceFor(xmppConnector.getConnection());
            chatManager.addChatListener((chat, createdLocally) -> {
                if (!createdLocally) {
                    chatObjectCallBack.callback(chat);
                }
            });
        }
    }

    public MultiUserChat createGroupChat(String room, String ownerJid) throws XMPPException.XMPPErrorException, SmackException {
        MultiUserChat chatRoom = MultiUserChatManager.getInstanceFor(xmppConnector.getConnection()).getMultiUserChat(ownerJid);
        chatRoom.create("test");
        Form form = chatRoom.getConfigurationForm().createAnswerForm();
        form.setAnswer("muc#roomconfig_publicroom", true);
        form.setAnswer("muc#roomconfig_roomname", "room786");
        form.setAnswer("muc#roomconfig_roomowners", "owners");
        form.setAnswer("muc#roomconfig_persistentroom", true);
        chatRoom.sendConfigurationForm(form);
        MultiUserChatManager.getInstanceFor(xmppConnector.getConnection()).addInvitationListener(new GroupChatListener());
        return chatRoom;
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

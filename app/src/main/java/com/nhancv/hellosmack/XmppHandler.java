package com.nhancv.hellosmack;

import android.util.Log;

import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.listener.ICollections;
import com.nhancv.hellosmack.listener.XMPPStanzaListener;
import com.nhancv.hellosmack.model.User;

import org.jivesoftware.smack.AbstractXMPPConnection;
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
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by nhancao on 9/5/16.
 */
public class XmppHandler {
    private static final String TAG = XmppHandler.class.getSimpleName();
    private static XmppHandler instance = new XmppHandler();

    AbstractXMPPConnection connection;
    private List<User> userList = new ArrayList<>();

    public static XmppHandler getInstance() {
        return instance;
    }

    public List<User> getUserList() {
        return userList;
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
                    NUtil.runOnUi(() -> {
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
        NUtil.aSyncTask(subscriber -> {
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

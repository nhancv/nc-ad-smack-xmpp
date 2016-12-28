package com.nhancv.xmpp;

import android.support.annotation.NonNull;

import com.nhancv.xmpp.listener.XmppListener;
import com.nhancv.xmpp.model.BaseError;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoom;
import com.nhancv.xmpp.model.BaseRoster;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by nhancao on 12/13/16.
 */

public interface IXmppPresenter {

    IXmppConnector getXmppConnector();

    //Credential
    void login(String username, String passwod, XmppListener.IXmppLoginListener loginConnectionListener)
            throws XMPPException, IOException, SmackException;

    void createUser(String userJid, String password, XmppListener.IXmppCreateListener createConnectionListener)
            throws XMPPException, IOException, SmackException;

    void logout();

    void connectionListenerRegister(ConnectionListener connectionListener);

    void enableCarbonMessage();

    void disableCarbonMessage();

    boolean isConnected();

    String getCurrentUser();

    //Invite/request
    void updatePresence(Presence.Mode presenceMode, String status);

    void sendStanza(@NonNull Stanza packet);

    void sendInviteRequest(String userJid);

    void acceptInviteRequest(String userJid);

    void sendUnFriendRequest(String userJid);

    void acceptUnFriendRequest(String userJid);

    //Listener
    void removeAsyncStanzaListener(StanzaListener listener);

    void removeAsyncStanzaListener(StanzaPackageType packetListener);

    void addAsyncStanzaListener(StanzaPackageType packetListener);

    void addMessageStanzaListener(XmppListener.IXmppCallback<BaseMessage> messageStanzaListener);

    void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter);

    void addListStanzaListener(List<StanzaPackageType> packetListeners);

    void setAutoAcceptSubscribe();

    //Chat
    MultiUserChat createGroupChat(String groupName, String description, String roomId, String ownerJid,
                                  XmppListener.CreateGroupListener createGroupListener,
                                  XmppListener.ParticipantListener participantListener)
            throws XMPPException.XMPPErrorException, SmackException;

    void leaveRoom(MultiUserChat muc);

    BaseError joinRoom(MultiUserChat muc, XmppListener.ParticipantListener participantListener, String nickName);

    void mucListenerRegister(MultiUserChat muc, final XmppListener.ParticipantListener participantListener);

    Chat openChatSession(StanzaListener listener, String toJid);

    void closeChatSession(StanzaListener listener);

    MultiUserChatManager getMultiUserChatManager();

    DeliveryReceiptManager getDeliveryReceiptManager();

    OfflineMessageManager getOfflineMessageManager();

    ChatStateManager getChatStateManager();

    ChatManager getChatManager();

    ChatRoomStateManager getChatRoomStateManager();

    List<BaseMessage> getMessageList(String jid);

    List<BaseRoom> getRoomList();

    BaseRoom getRoom(String roomId);

    void refreshRoomListMap();

    //User list
    Roster setupRosterList(@NonNull RosterListener rosterListener);

    Roster setupRosterList(@NonNull XmppListener.IXmppCallback<BaseRoster> updateListener);

    Roster setupRosterList(RosterListener rosterListener, XmppListener.IXmppCallback<BaseRoster> updateListener);

    List<BaseRoster> getCurrentRosterList();

    BaseRoster getRoster(String rosterJid);

}

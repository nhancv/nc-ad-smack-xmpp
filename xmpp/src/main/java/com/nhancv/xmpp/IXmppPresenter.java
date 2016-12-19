package com.nhancv.xmpp;

import android.support.annotation.NonNull;

import com.nhancv.xmpp.listener.XmppListener;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoster;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

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

    void logout() throws SmackException.NotConnectedException;

    String getCurrentUser();

    //Invite/request
    void updatePresence(Presence.Mode presenceMode, String status) throws SmackException.NotConnectedException;

    void sendStanza(@NonNull Stanza packet) throws SmackException.NotConnectedException;

    void sendInviteRequest(String userJid) throws SmackException.NotConnectedException;

    void acceptInviteRequest(String userJid) throws SmackException.NotConnectedException;

    void sendUnFriendRequest(String userJid) throws SmackException.NotConnectedException;

    void acceptUnFriendRequest(String userJid) throws SmackException.NotConnectedException;

    //Listener
    void removeAsyncStanzaListener(StanzaListener listener);

    void removeAsyncStanzaListener(StanzaPackageType packetListener);

    void addAsyncStanzaListener(StanzaPackageType packetListener);

    void addMessageStanzaListener(XmppListener.IXmppCallback<Stanza> messageStanzaListener);

    void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter);

    void addListStanzaListener(List<StanzaPackageType> packetListeners);

    void setAutoAcceptSubscribe();

    //Chat
    Chat openChatSession(StanzaListener listener, String toJid);

    void closeChatSession(StanzaListener listener);

    OfflineMessageManager getOfflineMessageManager();

    ChatStateManager getChatStateManager();

    List<BaseMessage> getMessageList(String jid);

    //User list
    Roster setupRosterList(@NonNull RosterListener rosterListener);

    Roster setupRosterList(@NonNull XmppListener.IXmppUpdateCallback updateListener);

    Roster setupRosterList(RosterListener rosterListener, XmppListener.IXmppUpdateCallback updateListener);

    List<BaseRoster> getCurrentRosterList();

    BaseRoster getRoster(String rosterJid);


}

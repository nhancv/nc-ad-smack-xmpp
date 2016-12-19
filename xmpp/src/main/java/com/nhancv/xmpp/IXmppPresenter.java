package com.nhancv.xmpp;

import android.support.annotation.NonNull;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

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

    void addAsyncStanzaListener(StanzaPackageType packetListener);

    void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter);

    void addListStanzaListener(List<StanzaPackageType> packetListeners);

    void setAutoAcceptSubscribe();

    //Chat
    Chat preparingChat(String toJid);

    void openChatSession(StanzaListener listener, String toJid);

    void closeChatSession(StanzaListener listener);

    //User list
    Roster setupRosterList(RosterListener rosterListener);

    List<BaseRoster> getCurrentRosterList();

    BaseRoster getRoster(String rosterJid);



}

package com.nhancv.hellosmack.xmpp;

import android.support.annotation.NonNull;

import com.nhancv.hellosmack.listener.ICollections;
import com.nhancv.hellosmack.listener.XMPPStanzaListener;
import com.nhancv.hellosmack.model.User;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;

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

    String getCurrentUser();

    //Invite/request
    void sendStanza(@NonNull Stanza packet) throws SmackException.NotConnectedException;

    void sendInviteRequest(String userJid) throws SmackException.NotConnectedException;

    void acceptInviteRequest(String userJid) throws SmackException.NotConnectedException;

    void sendUnFriendRequest(String userJid) throws SmackException.NotConnectedException;

    void acceptUnFriendRequest(String userJid) throws SmackException.NotConnectedException;

    //Listener
    void removeAsyncStanzaListener(StanzaListener listener);

    void addAsyncStanzaListener(XMPPStanzaListener packetListener);

    void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter packetFilter);

    void addListStanzaListener(List<XMPPStanzaListener> packetListeners);

    void setAutoAcceptSubscribe();

    //Chat
    Chat preparingChat(String toJid);

    void openChatSession(StanzaListener listener, String toJid);

    void closeChatSession(StanzaListener listener);

    //User list
    List<User> getUserList();

    void getUserList(ICollections.ObjectCallBack<Roster> listItemsCallback);



}

package com.nhancv.xmpp;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by nhancao on 12/26/16.
 */

public class ChatRoomStateManager extends Manager {
    public static final String NAMESPACE = "http://jabber.org/protocol/chatstates";

    private static final Map<XMPPConnection, ChatRoomStateManager> INSTANCES =
            new WeakHashMap<>();

    private static final StanzaFilter filter = new NotFilter(new StanzaExtensionFilter(NAMESPACE));
    private final Map<MultiUserChat, ChatState> chatStates = new WeakHashMap<>();

    public ChatRoomStateManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(NAMESPACE);
        INSTANCES.put(connection, this);
    }

    public static synchronized ChatRoomStateManager getInstance(final XMPPConnection connection) {
        ChatRoomStateManager manager = INSTANCES.get(connection);
        if (manager == null) {
            manager = new ChatRoomStateManager(connection);
        }
        return manager;
    }

    public void setCurrentState(ChatState newState, MultiUserChat muc) throws SmackException.NotConnectedException {
        if (muc == null || newState == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        if (!updateChatState(muc, newState)) {
            return;
        }
        Message message = new Message();
        ChatStateExtension extension = new ChatStateExtension(newState);
        message.addExtension(extension);
        muc.sendMessage(message);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoomStateManager that = (ChatRoomStateManager) o;

        return connection().equals(that.connection());

    }

    public int hashCode() {
        return connection().hashCode();
    }

    private synchronized boolean updateChatState(MultiUserChat muc, ChatState newState) {
        ChatState lastChatState = chatStates.get(muc);
        if (lastChatState != newState) {
            chatStates.put(muc, newState);
            return true;
        }
        return false;
    }


}

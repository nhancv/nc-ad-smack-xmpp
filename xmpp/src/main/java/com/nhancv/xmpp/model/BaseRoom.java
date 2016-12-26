package com.nhancv.xmpp.model;

import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import java.util.List;

/**
 * Created by nhancao on 12/22/16.
 */

public class BaseRoom {
    private String roomJid;
    private String roomNick;
    private MultiUserChat multiUserChat;
    private List<Occupant> members;
    private String lastMessage;

    public BaseRoom(MultiUserChat multiUserChat, List<Occupant> members) {
        this.multiUserChat = multiUserChat;
        this.roomJid = multiUserChat.getRoom();
        this.roomNick = multiUserChat.getNickname();
        this.members = members;
    }

    public String getRoomJid() {
        return roomJid;
    }

    public String getRoomNick() {
        return roomNick;
    }

    public List<Occupant> getMembers() {
        return members;
    }

    public MultiUserChat getMultiUserChat() {
        return multiUserChat;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

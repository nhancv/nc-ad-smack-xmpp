package com.nhancv.xmpp.model;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by nhancao on 12/21/16.
 */

public class BaseInvitation {

    private XMPPConnection conn;
    private MultiUserChat room;
    private String inviter;
    private String reason;
    private String password;
    private Message message;

    public BaseInvitation(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
        this.conn = conn;
        this.room = room;
        this.inviter = inviter;
        this.reason = reason;
        this.password = password;
        this.message = message;
    }

    public XMPPConnection getConn() {
        return conn;
    }

    public MultiUserChat getRoom() {
        return room;
    }

    public String getInviter() {
        return inviter;
    }

    public String getReason() {
        return reason;
    }

    public String getPassword() {
        return password;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "conn=" + conn +
                ", room=" + room +
                ", inviter='" + inviter + '\'' +
                ", reason='" + reason + '\'' +
                ", password='" + password + '\'' +
                ", message=" + message +
                '}';
    }
}
package com.nhancv.xmpp.model;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by nhancao on 12/19/16.
 */

public class BaseMessage {

    private Message message;
    private boolean read;

    public BaseMessage(Message message) {
        this(message, false);
    }

    public BaseMessage(Message message, boolean read) {
        this.message = message;
        this.read = read;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

package com.nhancv.xmpp.model;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by nhancao on 12/19/16.
 */

public class BaseMessage {

    private Message message;
    private boolean read;
    private boolean delivered;
    private boolean seen;

    public BaseMessage(Message message) {
        this(message, false);
    }

    public BaseMessage(Message message, boolean read) {
        this.message = message;
        this.read = read;
        this.seen = false;
        this.delivered = false;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
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

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}

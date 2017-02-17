package com.nhancv.xmpp.model;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by nhancao on 12/19/16.
 */

public class BaseMessage {

    private Message message;
    private ReadType readType;
    private SentType sentType;

    public BaseMessage(Message message) {
        this.message = message;
        this.readType = ReadType.UN_READ;
        this.sentType = SentType.SENT;
    }

    public BaseMessage(Message message, ReadType readType, SentType sentType) {
        this.message = message;
        this.readType = readType;
        this.sentType = sentType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ReadType getReadType() {
        return readType;
    }

    public void setReadType(ReadType readType) {
        this.readType = readType;
    }

    public SentType getSentType() {
        return sentType;
    }

    public void setSentType(SentType sentType) {
        this.sentType = sentType;
    }

    public boolean isRead() {
        return getReadType() == ReadType.READ;
    }

    public boolean isDelivered() {
        return getSentType() == SentType.DELIVERED;
    }

    public boolean isSeen() {
        return getSentType() == SentType.SEEN;
    }

    public enum ReadType {
        UN_READ,
        READ
    }

    public enum SentType {
        SENT,
        DELIVERED,
        SEEN
    }
}

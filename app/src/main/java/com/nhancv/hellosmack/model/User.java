package com.nhancv.hellosmack.model;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by nhancao on 9/6/16.
 */
public class User {
    private String name;
    private Presence presence;
    private String lastMessage;

    public User(String name, Presence presence) {
        this.name = name;
        this.presence = presence;
    }

    public String getName() {
        return name;
    }

    public Presence getPresence() {
        return presence;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

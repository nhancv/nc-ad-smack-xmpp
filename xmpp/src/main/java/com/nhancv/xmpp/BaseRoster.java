package com.nhancv.xmpp;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by nhancao on 9/6/16.
 */
public class BaseRoster {
    private String name;
    private Presence presence;
    private String lastMessage;

    public BaseRoster(String name, Presence presence) {
        this.name = name;
        this.presence = presence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}

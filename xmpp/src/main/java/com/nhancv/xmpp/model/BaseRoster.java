package com.nhancv.xmpp.model;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by nhancao on 9/6/16.
 */
public class BaseRoster {
    private String jid;
    private Presence presence;

    public BaseRoster(String jid, Presence presence) {
        this.jid = jid;
        this.presence = presence;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String name) {
        this.jid = name;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

}

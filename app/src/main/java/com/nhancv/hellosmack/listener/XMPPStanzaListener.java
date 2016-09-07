package com.nhancv.hellosmack.listener;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by nhancao on 9/7/16.
 */
public class XMPPStanzaListener {
    StanzaListener stanzaListener;
    Class<? extends Stanza> packetType;

    public XMPPStanzaListener(StanzaListener stanzaListener, Class<? extends Stanza> packetType) {
        this.stanzaListener = stanzaListener;
        this.packetType = packetType;
    }

    public StanzaListener getStanzaListener() {
        return stanzaListener;
    }

    public void setStanzaListener(StanzaListener stanzaListener) {
        this.stanzaListener = stanzaListener;
    }

    public Class<? extends Stanza> getPacketType() {
        return packetType;
    }

    public void setPacketType(Class<? extends Stanza> packetType) {
        this.packetType = packetType;
    }
}

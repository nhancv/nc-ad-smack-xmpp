package com.nhancv.xmpp;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by nhancao on 9/7/16.
 */
public class StanzaPackageType {
    private StanzaListener stanzaListener;
    private Class<? extends Stanza> packetType;

    public StanzaPackageType(StanzaListener stanzaListener, Class<? extends Stanza> packetType) {
        this.stanzaListener = stanzaListener;
        this.packetType = packetType;
    }

    public StanzaListener getStanzaListener() {
        return stanzaListener;
    }

    public Class<? extends Stanza> getPacketType() {
        return packetType;
    }

}

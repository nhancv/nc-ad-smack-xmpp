package com.nhancv.xmpp;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppConfig implements IXmppConfig {

    @Override
    public String getHost() {
        return "jabber.to";
    }

    @Override
    public String getDomain() {
        return "jabber.to";
    }

    @Override
    public Integer getPort() {
        return 5269;
    }
}

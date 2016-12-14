package com.nhancv.xmpp;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppConfig implements IXmppConfig {

    private static final String HOST = "local.beesightsoft.com";
    private static final String DOMAIN = "local.beesightsoft.com";
    private static final int PORT = 7008;

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public String getDomain() {
        return DOMAIN;
    }

    @Override
    public Integer getPort() {
        return PORT;
    }
}

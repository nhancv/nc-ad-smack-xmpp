package com.nhancv.hellosmack.xmpp;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppCredential implements IXmppCredential {

    private String username;
    private String password;

    public XmppCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}

package com.nhancv.hellosmack.model;

import com.google.gson.Gson;
import com.nhancv.xmpp.model.BaseNotify;

/**
 * Created by nhancao on 1/18/17.
 */

public class Notify extends BaseNotify {

    public Notify(String content) {
        super(content);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

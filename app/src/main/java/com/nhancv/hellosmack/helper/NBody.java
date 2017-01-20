package com.nhancv.hellosmack.helper;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by nhancao on 1/18/17.
 */

public class NBody implements Serializable {

    private String type; //chat, file, image, notify
    private boolean read;
    private String content;

    public NBody(String type, boolean read, String content) {
        this.type = type;
        this.read = read;
        this.content = content;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

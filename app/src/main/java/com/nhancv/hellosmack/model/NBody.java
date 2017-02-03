package com.nhancv.hellosmack.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nhancao on 1/18/17.
 */

public class NBody implements Serializable {

    @SerializedName("type")
    private String type; //text, file, image, notify
    @SerializedName("read")
    private boolean read;
    @SerializedName("seen")
    private boolean seen;
    @SerializedName("content")
    private String content;

    public NBody(String type, String content) {
        this.type = type;
        this.read = false;
        this.seen = false;
        this.content = content;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

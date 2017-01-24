package com.nhancv.xmpp.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nhancao on 1/18/17.
 */

public class BaseNotify implements Serializable {

    @SerializedName("content")
    private String content;
    @SerializedName("type")
    private String type;
    @SerializedName("proc_type")
    private String procType;

    public BaseNotify(String content) {
        this.content = content;
        this.type = "notify";
        this.procType = "read_message";
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getProcType() {
        return procType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

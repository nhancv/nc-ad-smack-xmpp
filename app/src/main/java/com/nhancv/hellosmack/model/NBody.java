package com.nhancv.hellosmack.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nhancao on 1/18/17.
 */

public class NBody implements Serializable {

    @SerializedName("type")
    private String type; //text, image, notify
    @SerializedName("read")
    private boolean read;
    @SerializedName("seen")
    private boolean seen;
    @SerializedName("content")
    private String content;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;

    public NBody(String from, String to, String type, String content, long timestamp) {
        this.type = type;
        this.read = false;
        this.seen = false;
        this.content = content;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
    }

    public static NBody parseFromBody(String body) {
        return new Gson().fromJson(body, NBody.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isSeen() {
        return seen;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}

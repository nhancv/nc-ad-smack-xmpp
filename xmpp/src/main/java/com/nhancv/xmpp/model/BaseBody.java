package com.nhancv.xmpp.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nhancao on 1/18/17.
 */

public class BaseBody implements Serializable {

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
    @SerializedName("id")
    private String id;


    public BaseBody(String from, String to, String type, String content, long timestamp, String id) {
        this.type = type;
        this.read = false;
        this.seen = false;
        this.content = content;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.id = id;
    }

    public static BaseBody from(BaseMessage baseMessage) {
        return fromJson(baseMessage.getMessage().getBody());
    }

    public static BaseBody fromJson(String body) {
        return new Gson().fromJson(body, BaseBody.class);
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

    public String getId() {
        return id;
    }
}

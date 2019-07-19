package com.rcircle.service.gateway.model;

import java.io.Serializable;

public class Message implements Serializable {
    public static final int TYPE_SMS = 1;
    public static final int TYPE_NEWS = 2;
    public static final int TYPE_NEWS_IMP = 3;
    public static final int TYPE_SYS = 4;
    public static final int TYPE_SYS_IMP = 5;
    private long id;
    private String title;
    private String sender;
    private int sender_uid;
    private int receiver_uid;
    private String content;
    private long date;
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setAuthor(String author) {
        setSender(author);
    }

    public void setSender_name(String sender_name){
        setSender(sender_name);
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setUrl(String url) {
        setContent(url);
    }

    public void setMessage(String message){
        setContent(message);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(int sender_uid) {
        this.sender_uid = sender_uid;
    }

    public int getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(int receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

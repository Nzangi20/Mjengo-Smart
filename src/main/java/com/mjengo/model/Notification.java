package com.mjengo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Notification model for system alerts, messages, and events.
 */
public class Notification {

    private static final AtomicLong ID_GEN = new AtomicLong(1);

    private long id;
    private String userId; // target user email
    private String type; // ALERT, MESSAGE, INFO, TASK
    private String message;
    private String link;
    private boolean read;
    private String timestamp;

    public Notification() {
        this.id = ID_GEN.getAndIncrement();
    }

    public Notification(String userId, String type, String message, String link, String timestamp) {
        this.id = ID_GEN.getAndIncrement();
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.link = link;
        this.read = false;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

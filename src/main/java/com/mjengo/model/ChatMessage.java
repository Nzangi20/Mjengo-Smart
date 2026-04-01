package com.mjengo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Chat message model for project-based communication.
 */
public class ChatMessage {

    private static final AtomicLong ID_GEN = new AtomicLong(1);

    private long id;
    private String projectId;
    private String senderName;
    private String senderRole;
    private String content;
    private String timestamp;

    public ChatMessage() {
        this.id = ID_GEN.getAndIncrement();
    }

    public ChatMessage(String projectId, String senderName, String senderRole, String content, String timestamp) {
        this.id = ID_GEN.getAndIncrement();
        this.projectId = projectId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

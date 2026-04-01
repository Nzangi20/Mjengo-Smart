package com.mjengo.dto;

import com.mjengo.model.ChatMessage;

/**
 * Serializable payload for WebSocket broadcast of chat messages.
 */
public class ChatMessageDto {

    private long id;
    private String projectId;
    private String senderName;
    private String senderRole;
    private String content;
    private String timestamp;

    public static ChatMessageDto from(ChatMessage m) {
        ChatMessageDto d = new ChatMessageDto();
        d.setId(m.getId());
        d.setProjectId(m.getProjectId());
        d.setSenderName(m.getSenderName());
        d.setSenderRole(m.getSenderRole());
        d.setContent(m.getContent());
        d.setTimestamp(m.getTimestamp());
        return d;
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

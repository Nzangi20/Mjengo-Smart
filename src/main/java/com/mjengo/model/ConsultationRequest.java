package com.mjengo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Client-requested consultation with a specific role (engineer, PM, etc.).
 */
public class ConsultationRequest {

    private static final AtomicLong ID_GEN = new AtomicLong(1);

    private long id;
    private String clientEmail;
    private String clientName;
    private long projectId;
    private String projectName;
    /** Target role: ENGINEER, PROJECT_MANAGER, ADMIN, CONTRACTOR */
    private String consulteeRole;
    private String topic;
    private String preferredSlot;
    private String notes;
    private String status;
    private String createdAt;

    public ConsultationRequest() {
        this.id = ID_GEN.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConsulteeRole() {
        return consulteeRole;
    }

    public void setConsulteeRole(String consulteeRole) {
        this.consulteeRole = consulteeRole;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPreferredSlot() {
        return preferredSlot;
    }

    public void setPreferredSlot(String preferredSlot) {
        this.preferredSlot = preferredSlot;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static void syncIdGenerator(long nextId) {
        ID_GEN.set(Math.max(1, nextId));
    }
}

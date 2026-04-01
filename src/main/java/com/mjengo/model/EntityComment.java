package com.mjengo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Threaded discussion on tasks, reports, or design artifacts.
 */
public class EntityComment {

    private static final AtomicLong ID_GEN = new AtomicLong(1);

    private long id;
    /** e.g. TASK:42, REPORT:Westlands, DESIGN:main */
    private String targetKey;
    private String authorName;
    private String authorRole;
    private String body;
    private String createdAt;

    public EntityComment() {
        this.id = ID_GEN.getAndIncrement();
    }

    public EntityComment(String targetKey, String authorName, String authorRole, String body, String createdAt) {
        this.id = ID_GEN.getAndIncrement();
        this.targetKey = targetKey;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.body = body;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

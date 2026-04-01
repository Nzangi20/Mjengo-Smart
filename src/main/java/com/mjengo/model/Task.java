package com.mjengo.model;

public class Task {
    private long id;
    private String title;
    private String description;
    private String assignedTo;
    private String project;
    private String category; // Structural, Electrical, Plumbing, Quality, Design
    private String priority; // HIGH, MEDIUM, LOW
    private String status; // OVERDUE, TODAY, UPCOMING, DONE
    private String dueDate;

    public Task() {
    }

    public Task(long id, String title, String description, String assignedTo, String project,
            String category, String priority, String status, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.project = project;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}

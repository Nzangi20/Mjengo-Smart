package com.mjengo.model;

public class Incident {
    private long id;
    private String title;
    private String severity; // MINOR, MODERATE, MAJOR, CRITICAL
    private String type; // NEAR_MISS, PROPERTY_DAMAGE, INJURY, ENVIRONMENTAL
    private String description;
    private String project;
    private String reportedBy;
    private String date;
    private String actionTaken;

    public Incident() {
    }

    public Incident(long id, String title, String severity, String type, String description,
            String project, String reportedBy, String date, String actionTaken) {
        this.id = id;
        this.title = title;
        this.severity = severity;
        this.type = type;
        this.description = description;
        this.project = project;
        this.reportedBy = reportedBy;
        this.date = date;
        this.actionTaken = actionTaken;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }
}

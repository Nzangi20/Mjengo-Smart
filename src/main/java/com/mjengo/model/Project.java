package com.mjengo.model;

/**
 * Represents a construction project in the system.
 */
public class Project {
    private long id;
    private String name;
    private String location;
    private String status; // ON_TRACK, DELAYED, AT_RISK, COMPLETED
    private double budget;
    private double spent;
    private int progressPercent;
    private String projectManager;
    private String startDate;
    private String endDate;
    private String description;

    /** RESIDENTIAL, COMMERCIAL, INDUSTRIAL, INFRASTRUCTURE */
    private String constructionType = "RESIDENTIAL";
    /** Owning client user email (for approvals & visibility) */
    private String clientEmail = "";
    private String requirements = "";
    /** PENDING, APPROVED, REJECTED */
    private String designApproval = "PENDING";
    private String budgetApproval = "PENDING";
    private String timelineApproval = "PENDING";
    private long siteAnalysisResultId = 0;
    private long costEstimateId = 0;

    public Project() {
    }

    public Project(long id, String name, String location, String status, double budget, double spent,
            int progressPercent, String projectManager, String startDate, String endDate, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.budget = budget;
        this.spent = spent;
        this.progressPercent = progressPercent;
        this.projectManager = projectManager;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConstructionType() {
        return constructionType;
    }

    public void setConstructionType(String constructionType) {
        this.constructionType = constructionType;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getDesignApproval() {
        return designApproval;
    }

    public void setDesignApproval(String designApproval) {
        this.designApproval = designApproval;
    }

    public String getBudgetApproval() {
        return budgetApproval;
    }

    public void setBudgetApproval(String budgetApproval) {
        this.budgetApproval = budgetApproval;
    }

    public String getTimelineApproval() {
        return timelineApproval;
    }

    public void setTimelineApproval(String timelineApproval) {
        this.timelineApproval = timelineApproval;
    }

    public long getSiteAnalysisResultId() {
        return siteAnalysisResultId;
    }

    public void setSiteAnalysisResultId(long siteAnalysisResultId) {
        this.siteAnalysisResultId = siteAnalysisResultId;
    }

    public long getCostEstimateId() {
        return costEstimateId;
    }

    public void setCostEstimateId(long costEstimateId) {
        this.costEstimateId = costEstimateId;
    }
}

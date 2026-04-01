package com.mjengo.model;

import java.util.List;

/**
 * Result of site suitability analysis.
 * Contains scores, risks, recommendations, and construction guidance.
 */
public class SiteAnalysisResult {

    private long id;
    /** Linked construction project (0 = none) */
    private long projectId;
    private String siteName;
    private double latitude;
    private double longitude;
    private double suitabilityScore; // 0-100
    private String status; // SUITABLE, MODERATE, NOT_SUITABLE
    private String statusColor; // emerald, amber, red
    private List<String> risks;
    private List<String> recommendations;
    private String foundationType;
    private String costImpact; // LOW, MODERATE, HIGH
    private double costMultiplier; // 1.0 - 1.5
    private String analyzedAt;

    // Individual scores
    private int soilScore;
    private int slopeScore;
    private int floodScore;
    private int accessScore;
    private int waterTableScore;
    private int seismicScore;
    private int utilitiesScore;

    public SiteAnalysisResult() {
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public double getSuitabilityScore() {
        return suitabilityScore;
    }

    public void setSuitabilityScore(double suitabilityScore) {
        this.suitabilityScore = suitabilityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public List<String> getRisks() {
        return risks;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getFoundationType() {
        return foundationType;
    }

    public void setFoundationType(String foundationType) {
        this.foundationType = foundationType;
    }

    public String getCostImpact() {
        return costImpact;
    }

    public void setCostImpact(String costImpact) {
        this.costImpact = costImpact;
    }

    public double getCostMultiplier() {
        return costMultiplier;
    }

    public void setCostMultiplier(double costMultiplier) {
        this.costMultiplier = costMultiplier;
    }

    public String getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(String analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public int getSoilScore() {
        return soilScore;
    }

    public void setSoilScore(int soilScore) {
        this.soilScore = soilScore;
    }

    public int getSlopeScore() {
        return slopeScore;
    }

    public void setSlopeScore(int slopeScore) {
        this.slopeScore = slopeScore;
    }

    public int getFloodScore() {
        return floodScore;
    }

    public void setFloodScore(int floodScore) {
        this.floodScore = floodScore;
    }

    public int getAccessScore() {
        return accessScore;
    }

    public void setAccessScore(int accessScore) {
        this.accessScore = accessScore;
    }

    public int getWaterTableScore() {
        return waterTableScore;
    }

    public void setWaterTableScore(int waterTableScore) {
        this.waterTableScore = waterTableScore;
    }

    public int getSeismicScore() {
        return seismicScore;
    }

    public void setSeismicScore(int seismicScore) {
        this.seismicScore = seismicScore;
    }

    public int getUtilitiesScore() {
        return utilitiesScore;
    }

    public void setUtilitiesScore(int utilitiesScore) {
        this.utilitiesScore = utilitiesScore;
    }
}

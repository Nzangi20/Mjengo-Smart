package com.mjengo.model;

/**
 * Input data for site suitability analysis.
 * Captures environmental and accessibility factors.
 */
public class SiteSurveyData {

    private String projectId;
    private String siteName;
    private String soilType; // CLAY, SANDY, LOAM, ROCK, SILT, PEAT
    private double slopeAngle; // 0-45 degrees
    private String floodRisk; // LOW, MODERATE, HIGH
    private String accessibility; // EXCELLENT, GOOD, FAIR, POOR
    private String waterTable; // DEEP, MODERATE, SHALLOW
    private String seismicZone; // LOW, MODERATE, HIGH
    private double latitude;
    private double longitude;
    /** Roads / utilities provision: EXCELLENT, GOOD, FAIR, POOR */
    private String utilitiesRating = "GOOD";

    public SiteSurveyData() {
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public double getSlopeAngle() {
        return slopeAngle;
    }

    public void setSlopeAngle(double slopeAngle) {
        this.slopeAngle = slopeAngle;
    }

    public String getFloodRisk() {
        return floodRisk;
    }

    public void setFloodRisk(String floodRisk) {
        this.floodRisk = floodRisk;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getWaterTable() {
        return waterTable;
    }

    public void setWaterTable(String waterTable) {
        this.waterTable = waterTable;
    }

    public String getSeismicZone() {
        return seismicZone;
    }

    public void setSeismicZone(String seismicZone) {
        this.seismicZone = seismicZone;
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

    public String getUtilitiesRating() {
        return utilitiesRating;
    }

    public void setUtilitiesRating(String utilitiesRating) {
        this.utilitiesRating = utilitiesRating;
    }
}

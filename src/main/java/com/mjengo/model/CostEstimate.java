package com.mjengo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cost estimate for a construction project.
 * Contains line items grouped by category with totals and adjustments.
 */
public class CostEstimate {

    private long id;
    private String projectName;
    private String projectType; // RESIDENTIAL, COMMERCIAL, INDUSTRIAL, INFRASTRUCTURE
    private String location;
    private double areaSqm;
    private int floors;

    private List<CostLineItem> items = new ArrayList<>();

    private double materialTotal;
    private double laborTotal;
    private double equipmentTotal;
    private double subtotal;
    private double contingency; // 10% of subtotal
    private double vat; // 16% VAT
    private double grandTotal;

    private String generatedAt;

    /** Linked project id (0 = none) */
    private long linkedProjectId;
    /** Multiplier applied from site suitability (1.0 = none) */
    private double siteDifficultyMultiplier = 1.0;
    /** How site analysis influenced this estimate */
    private String integrationNotes = "";
    /**
     * When set, only this client may see this BoQ in read-only mode (unlinked drafts).
     * For linked projects, filled from the project's client email.
     */
    private String ownerClientEmail = "";

    public CostEstimate() {
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(double areaSqm) {
        this.areaSqm = areaSqm;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public List<CostLineItem> getItems() {
        return items;
    }

    public void setItems(List<CostLineItem> items) {
        this.items = items;
    }

    public double getMaterialTotal() {
        return materialTotal;
    }

    public void setMaterialTotal(double materialTotal) {
        this.materialTotal = materialTotal;
    }

    public double getLaborTotal() {
        return laborTotal;
    }

    public void setLaborTotal(double laborTotal) {
        this.laborTotal = laborTotal;
    }

    public double getEquipmentTotal() {
        return equipmentTotal;
    }

    public void setEquipmentTotal(double equipmentTotal) {
        this.equipmentTotal = equipmentTotal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getContingency() {
        return contingency;
    }

    public void setContingency(double contingency) {
        this.contingency = contingency;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public long getLinkedProjectId() {
        return linkedProjectId;
    }

    public void setLinkedProjectId(long linkedProjectId) {
        this.linkedProjectId = linkedProjectId;
    }

    public double getSiteDifficultyMultiplier() {
        return siteDifficultyMultiplier;
    }

    public void setSiteDifficultyMultiplier(double siteDifficultyMultiplier) {
        this.siteDifficultyMultiplier = siteDifficultyMultiplier;
    }

    public String getIntegrationNotes() {
        return integrationNotes;
    }

    public void setIntegrationNotes(String integrationNotes) {
        this.integrationNotes = integrationNotes;
    }

    public String getOwnerClientEmail() {
        return ownerClientEmail;
    }

    public void setOwnerClientEmail(String ownerClientEmail) {
        this.ownerClientEmail = ownerClientEmail != null ? ownerClientEmail : "";
    }
}

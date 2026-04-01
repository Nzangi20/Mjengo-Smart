package com.mjengo.model;

/**
 * Individual line item in a Bill of Quantities (BoQ).
 * Each item has a category, description, quantity, unit, rate, and total.
 */
public class CostLineItem {

    private int itemNo;
    private String category; // MATERIAL, LABOR, EQUIPMENT
    private String description;
    private double quantity;
    private String unit;
    private double rate;
    private double total;

    public CostLineItem() {
    }

    public CostLineItem(int itemNo, String category, String description, double quantity, String unit, double rate) {
        this.itemNo = itemNo;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.rate = rate;
        this.total = quantity * rate;
    }

    // Getters and Setters
    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

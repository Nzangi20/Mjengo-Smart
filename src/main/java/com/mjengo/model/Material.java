package com.mjengo.model;

public class Material {
    private long id;
    private String name;
    private String unit; // bags, pcs, cubic_m, kg, sheets
    private int currentStock;
    private int maxStock;
    private double unitPrice;
    private String supplier;
    private String status; // OK, LOW, CRITICAL

    public Material() {
    }

    public Material(long id, String name, String unit, int currentStock, int maxStock,
            double unitPrice, String supplier) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.currentStock = currentStock;
        this.maxStock = maxStock;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
        this.status = calculateStatus();
    }

    private String calculateStatus() {
        double ratio = (double) currentStock / maxStock;
        if (ratio <= 0.10)
            return "CRITICAL";
        if (ratio <= 0.25)
            return "LOW";
        return "OK";
    }

    public void recalculateStatus() {
        this.status = calculateStatus();
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
        recalculateStatus();
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

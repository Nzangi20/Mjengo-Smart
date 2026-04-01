package com.mjengo.model;

public class Transaction {
    private long id;
    private String transactionId;
    private String date;
    private String payee;
    private String category; // Materials, Labor, Permits, Equipment, Consulting
    private double amount;
    private String status; // CLEARED, PENDING, REJECTED
    private String project;

    public Transaction() {
    }

    public Transaction(long id, String transactionId, String date, String payee, String category,
            double amount, String status, String project) {
        this.id = id;
        this.transactionId = transactionId;
        this.date = date;
        this.payee = payee;
        this.category = category;
        this.amount = amount;
        this.status = status;
        this.project = project;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}

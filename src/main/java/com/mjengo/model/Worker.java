package com.mjengo.model;

public class Worker {
    private long id;
    private String name;
    private String trade; // Mason, Electrician, Plumber, Welder, Carpenter, Labourer
    private String phone;
    private String status; // PRESENT, ABSENT, LATE, ON_LEAVE
    private String checkInTime;
    private String assignedProject;
    private double dailyRate;

    public Worker() {
    }

    public Worker(long id, String name, String trade, String phone, String status,
            String checkInTime, String assignedProject, double dailyRate) {
        this.id = id;
        this.name = name;
        this.trade = trade;
        this.phone = phone;
        this.status = status;
        this.checkInTime = checkInTime;
        this.assignedProject = assignedProject;
        this.dailyRate = dailyRate;
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

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(String assignedProject) {
        this.assignedProject = assignedProject;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }
}

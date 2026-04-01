package com.mjengo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory User model with unique ID and session-based authentication.
 * Supports 5 roles: ADMIN, PROJECT_MANAGER, ENGINEER, CONTRACTOR, CLIENT.
 */
public class User {

    private static final AtomicLong ID_GEN = new AtomicLong(1);

    private long id;
    private String fullName;
    private String email;
    private String password;
    private String role; // ADMIN, PROJECT_MANAGER, ENGINEER, CONTRACTOR, CLIENT

    public User() {
        this.id = ID_GEN.getAndIncrement();
    }

    public User(String fullName, String email, String password, String role) {
        this.id = ID_GEN.getAndIncrement();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

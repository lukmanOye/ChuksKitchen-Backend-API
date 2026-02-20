package com.example.trueminds.model;

import com.example.trueminds.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class user {
    private String id;
    private String username;
    private String email;
    private String password;
    private String phone;
    private boolean verified;
    private referralCode referralCode;
    private LocalDateTime createdAt;
    private String userAddress;
    private Role role;
    private double balance;




    public user() {
        this.id = UUID.randomUUID().toString();
        this.verified = false;
        this.createdAt = LocalDateTime.now();
        this.role = Role.CUSTOMER;
        this.balance = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public com.example.trueminds.model.referralCode getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(com.example.trueminds.model.referralCode referralCode) {
        this.referralCode = referralCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}


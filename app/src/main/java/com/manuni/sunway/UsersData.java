package com.manuni.sunway;

public class UsersData {
    private String fullName,phoneNumber,email,uid,online,referCode,totalCount,adminMessage,timestamp,profileImage;
   private double balance=5.00;

    public UsersData() {
    }

    public UsersData(String fullName, String phoneNumber, String email, String uid, String online, String referCode, String totalCount, String adminMessage, String timestamp, String profileImage) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.uid = uid;
        this.online = online;
        this.referCode = referCode;
        this.totalCount = totalCount;
        this.adminMessage = adminMessage;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

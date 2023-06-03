package com.manuni.sunway;

public class WithdrawModel {
    private String uid,name,image,timestamp,status,withdraw,remainBalance,complete,count;

    public WithdrawModel() {
    }

    public WithdrawModel(String uid, String name, String image, String timestamp, String status, String withdraw, String remainBalance, String complete, String count) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.timestamp = timestamp;
        this.status = status;
        this.withdraw = withdraw;
        this.remainBalance = remainBalance;
        this.complete = complete;
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

    public String getRemainBalance() {
        return remainBalance;
    }

    public void setRemainBalance(String remainBalance) {
        this.remainBalance = remainBalance;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

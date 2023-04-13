package com.manuni.sunway;

public class TaskModel {
    private String packId,packKey,packName,userId;

    public TaskModel() {
    }

    public TaskModel(String packId, String packKey, String packName, String userId) {
        this.packId = packId;
        this.packKey = packKey;
        this.packName = packName;
        this.userId = userId;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getPackKey() {
        return packKey;
    }

    public void setPackKey(String packKey) {
        this.packKey = packKey;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.manuni.sunway;

public class PackageModel {
    private String id,perOrderQuantity,levelName;

    public PackageModel() {
    }

    public PackageModel(String id, String perOrderQuantity, String levelName) {
        this.id = id;
        this.perOrderQuantity = perOrderQuantity;
        this.levelName = levelName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerOrderQuantity() {
        return perOrderQuantity;
    }

    public void setPerOrderQuantity(String perOrderQuantity) {
        this.perOrderQuantity = perOrderQuantity;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}

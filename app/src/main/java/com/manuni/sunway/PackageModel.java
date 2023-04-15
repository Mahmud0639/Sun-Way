package com.manuni.sunway;

public class PackageModel {
    private String id,perOrderQuantity,levelName,packImage;

    public PackageModel() {
    }

    public PackageModel(String id, String perOrderQuantity, String levelName,String packImage) {
        this.id = id;
        this.perOrderQuantity = perOrderQuantity;
        this.levelName = levelName;
        this.packImage = packImage;
    }

    public String getPackImage() {
        return packImage;
    }

    public void setPackImage(String packImage) {
        this.packImage = packImage;
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

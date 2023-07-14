package com.manuni.sunway;

public class product_model {
    private String productImage,productNumber,productSellingPrice,productId,perOrder,packId;

    public product_model() {
    }

    public product_model(String productImage, String productNumber, String productSellingPrice, String productId,String perOrder) {
        this.productImage = productImage;
        this.productNumber = productNumber;
        this.productSellingPrice = productSellingPrice;
        this.productId = productId;
        this.perOrder = perOrder;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getPerOrder() {
        return perOrder;
    }

    public void setPerOrder(String perOrder) {
        this.perOrder = perOrder;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(String productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}

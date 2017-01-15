package com.hackathone.iotretail.warehouseserver;

public class Product {
    String qrCode;
    String productName;

    public Product(String qrCode, String productName) {
        this.qrCode = qrCode;
        this.productName = productName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

package com.varvet.barcodereadersample.camera;

/**
 * Created by amitp on 1/14/17.
 */
public class CartItem {
    String productName;
    int quantity;
    String qrCode;

    public CartItem(String discription, int quantity, String qrCode) {
        this.productName = discription;
        this.quantity = quantity;
        this.qrCode=qrCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}

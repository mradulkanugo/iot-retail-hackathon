package com.varvet.barcodereadersample.camera;

import java.util.List;


public class Cart {
    List<CartItem> itemList;
    public Cart(List<CartItem> itemList) {
        this.itemList = itemList;
    }

    public List<CartItem> getItemList() {
        return itemList;
    }
}

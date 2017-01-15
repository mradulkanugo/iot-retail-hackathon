package com.hackathone.iotretail.warehouseserver;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    @SerializedName("cart")
    Map<Product, Integer> itemQtyMap;

    public ShoppingCart() {
        itemQtyMap = new HashMap<>();
    }

    public void add(Product product) {
        Integer quantity = itemQtyMap.get(product);
        if(quantity == null)
            itemQtyMap.put(product, 0);
        itemQtyMap.put(product, quantity + 1);
    }
    public void add(Product product, Integer quantity){
        itemQtyMap.put(product, quantity);
    }
}

package com.varvet.barcodereadersample;

import android.util.Log;

import com.varvet.barcodereadersample.camera.Cart;
import com.varvet.barcodereadersample.camera.CartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class CartSerializer {

    static String serialize(Cart cart) throws JSONException {
        JSONObject postDataParams = new JSONObject();
        postDataParams.put("orderId", UUID.randomUUID().toString());
        postDataParams.put("name", "abc");
        postDataParams.put("email", "abc@gmail.com");

        JSONArray items = new JSONArray();
        for (CartItem item : cart.getItemList()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", item.getQrCode());
            jsonObject.put("productName", item.getProductName());
            jsonObject.put("quantity", item.getQuantity());
            items.put(jsonObject);
        }
        postDataParams.put("cart", items);
        return postDataParams.toString();
//        Log.e("params", postDataParams.toString());
    }
}

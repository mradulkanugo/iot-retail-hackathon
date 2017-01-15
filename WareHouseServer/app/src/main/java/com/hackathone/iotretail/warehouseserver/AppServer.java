package com.hackathone.iotretail.warehouseserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class AppServer extends NanoHTTPD {
    private final ItemRepository itemRepository = new ItemRepository();
    private BaseActivity baseActivity;

    public AppServer(BaseActivity baseActivity) throws IOException {
        super(8080);
        this.baseActivity = baseActivity;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

//     void startServer() throws IOException {
//        if (server == null) {
//            server = new AppServer();
//        }
//    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        Method method = session.getMethod();
        if (Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            } catch (ResponseException e) {
                e.printStackTrace();
                return newFixedLengthResponse(e.getStatus(), MIME_PLAINTEXT, e.getMessage());
            }
            String postBody = files.get("postData");
            System.out.println(postBody);
            Gson gson;
            gson = new GsonBuilder().registerTypeAdapter(Cart.class, new CartDeserializer(itemRepository)).create();
            try {
                Cart shoppingCart = gson.fromJson(postBody, Cart.class);
                postToBaseAcitvity(shoppingCart);
//            TaskQueue.add(shoppingCart);
            } catch (JsonParseException e) {
                e.printStackTrace();
                return newFixedLengthResponse("Invalid Json request");
            }
            return newFixedLengthResponse("Request recieved");
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
            JSONArray jsonArray = new JSONArray();
            for (Product product : itemRepository.allItems.keySet()) {
                JSONObject productJson = new JSONObject();
                productJson.put("productName", product.getProductName());
                productJson.put("productQrCode", product.getQrCode());
                jsonArray.put(productJson);
            }
                jsonObject.put("Products", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(jsonObject.toString());
        }
    }

    void postToBaseAcitvity(final Cart cart) {
        baseActivity.statusHandler.post(new Runnable() {
            @Override
            public void run() {
                baseActivity.addCarttoDispatchList(cart);
            }
        });
    }

}
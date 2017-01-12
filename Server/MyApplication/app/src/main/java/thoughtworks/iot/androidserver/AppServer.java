package thoughtworks.iot.androidserver;

import android.os.Bundle;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class AppServer extends NanoHTTPD {
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
        gson = new GsonBuilder().registerTypeAdapter(Cart.class, new CartDeserializer()).create();
        try {
             Cart shoppingCart = gson.fromJson(postBody, Cart.class);
            postToBaseAcitvity(shoppingCart);
//            TaskQueue.add(shoppingCart);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return newFixedLengthResponse("Invalid Json request");
        }
        return newFixedLengthResponse("Request recieved");
    }

    void postToBaseAcitvity(final Cart cart){
        baseActivity.statusHandler.post(new Runnable() {
            @Override
            public void run() {
                baseActivity.addCarttoDispatchList(cart);
            }
        });
    }

}
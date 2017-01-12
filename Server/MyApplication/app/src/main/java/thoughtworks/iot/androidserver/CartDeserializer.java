package thoughtworks.iot.androidserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class CartDeserializer implements JsonDeserializer<Cart> {
//    private final ProductService productService;

//    public CartDeserializer(ProductService productService) {
//        this.productService = productService;
//    }

    @Override
    public Cart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        ShoppingCart cart = new ShoppingCart();
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonCartArray = jsonObject.getAsJsonArray("cart");
        Cart cart = new Cart(0, 0, 0, 0);
        for (JsonElement jsonElement : jsonCartArray) {
            JsonObject cartObject = jsonElement.getAsJsonObject();
            String productID = cartObject.get("productID").getAsString();
            //Product product = productService.getProduct(productID);
//            if(product == null){
//                throw new JsonParseException("Invalid QR Code");
//            }
            int quantity = cartObject.get("quantity").getAsInt();
            cart.itemList[Integer.parseInt(productID)] = quantity;
                    //cart.add(product, quantity);
        }

        return cart;
    }
}

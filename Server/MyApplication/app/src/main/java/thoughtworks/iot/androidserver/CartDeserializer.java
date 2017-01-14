package thoughtworks.iot.androidserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class CartDeserializer implements JsonDeserializer<Cart> {
    private final ItemRepository itemRepository;

    public CartDeserializer(ItemRepository productService) {
        this.itemRepository = productService;
    }

    @Override
    public Cart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        ShoppingCart cart = new ShoppingCart();
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonCartArray = jsonObject.getAsJsonArray("cart");
        Cart cart = new Cart(0, 0, 0, 0);
        for (JsonElement jsonElement : jsonCartArray) {
            JsonObject cartObject = jsonElement.getAsJsonObject();
            String productID = cartObject.get("productID").getAsString();
            Integer shelfNumber = itemRepository.getShelfNumber(productID);
            if(shelfNumber == null){
                throw new JsonParseException("Invalid QR Code");
            }
            int quantity = cartObject.get("quantity").getAsInt();
            cart.itemList[shelfNumber] = quantity;
                    //cart.add(product, quantity);
        }

        return cart;
    }
}

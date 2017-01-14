package thoughtworks.iot.androidserver;


import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    Map<Product, Integer> allItems;

    public ItemRepository() {
        allItems = new HashMap<>();
        allItems.put(new Product("1ABCDE", "ParleG"), 0);
        allItems.put(new Product("2ABCDE", "CenterFresh"), 1);
        allItems.put(new Product("3ABCDE", "Nirma"), 2);
        allItems.put(new Product("4ABCDE", "Apsara"), 3);
    }


    public Integer getShelfNumber(String qrCode) {
        for (Product product : allItems.keySet()) {
            if(product.getQrCode().equals(qrCode))
                return allItems.get(product);
        }
        return null;
    }
}

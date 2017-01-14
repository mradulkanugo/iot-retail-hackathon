package thoughtworks.iot.androidserver;


import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    Map<String, Integer> allItems;

    public ItemRepository() {
        allItems = new HashMap<>();
        allItems.put(new String("1ABCDE"), 0);
        allItems.put(new String("2ABCDE"), 1);
        allItems.put(new String("3ABCDE"), 2);
        allItems.put(new String("4ABCDE"), 3);
    }


    public Integer getShelfNumber(String qrCode) {
        return  allItems.get(qrCode);
    }
}

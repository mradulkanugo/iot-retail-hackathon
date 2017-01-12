package thoughtworks.iot.androidserver;

/**
 * Created by mradulkanugo on 12/01/17.
 */

public class Cart {
    int numberOfitem;
    int[] itemList;
    public  Cart(int numberOfitem1,int numberOfitem2,int numberOfitem3,int numberOfitem4){
        itemList=new int[4];
        itemList[0]=numberOfitem1;
        itemList[1]=numberOfitem2;
        itemList[2]=numberOfitem3;
        itemList[3]=numberOfitem4;
        numberOfitem=numberOfitem1+numberOfitem2+numberOfitem3+numberOfitem4;
    }
}

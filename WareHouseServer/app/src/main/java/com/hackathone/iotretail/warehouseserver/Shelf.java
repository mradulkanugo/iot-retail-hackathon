package com.hackathone.iotretail.warehouseserver;

/**
 * Created by mradulkanugo on 12/01/17.
 */

public class Shelf {
    int shelfSize=4;
    int shelfNumber;
    int servoNumber;
    int numberOfItem;
    int[] angleForItemNumber;
    BaseActivity activity;
    ShelfView shelfView;

    public Shelf(int shelfNumber,int servoNumber,int numberOfItem,int[] angleForItemNumber,BaseActivity baseActivity,ShelfView shelfView){
        this.shelfNumber=shelfNumber;
        this.servoNumber=servoNumber;
        this.numberOfItem=numberOfItem;
        this.angleForItemNumber=angleForItemNumber;
        this.activity=baseActivity;
        this.shelfView=shelfView;
      //  this.shelfView.setIteminShelf(this.numberOfItem,shelfSize,(this.shelfNumber-1)/2);
    }

    public void dropItemOnBelt(int numberOfItemToDrop){
        int remainingItem=numberOfItem-numberOfItemToDrop;
        if(activity.bluetoothThread!=null) {
            activity.bluetoothThread.turnMotorByDegrees(servoNumber, angleForItemNumber[remainingItem]);
        }
        numberOfItem=remainingItem;
        shelfView.setIteminShelf(numberOfItem,shelfSize,(shelfNumber-1)/2);
        activity.trackView.addItemOnTrack((float)((shelfNumber%2)*50+25),shelfNumber);
    }


}

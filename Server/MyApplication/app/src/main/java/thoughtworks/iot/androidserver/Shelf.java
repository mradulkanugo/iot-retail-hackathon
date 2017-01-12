package thoughtworks.iot.androidserver;

/**
 * Created by mradulkanugo on 12/01/17.
 */

public class Shelf {
    int shelfNumber;
    int servoNumber;
    int numberOfItem;
    int[] angleForItemNumber;
    BaseActivity activity;

    public Shelf(int shelfNumber,int servoNumber,int numberOfItem,int[] angleForItemNumber,BaseActivity baseActivity){
        this.shelfNumber=shelfNumber;
        this.servoNumber=servoNumber;
        this.numberOfItem=numberOfItem;
        this.angleForItemNumber=angleForItemNumber;
        this.activity=baseActivity;
    }

    public void dropItemOnBelt(int numberOfItemToDrop){
        int remainingItem=numberOfItem-numberOfItemToDrop;
        activity.bluetoothThread.turnMotorByDegrees(servoNumber,angleForItemNumber[remainingItem]);
        numberOfItem=remainingItem;
    }


}

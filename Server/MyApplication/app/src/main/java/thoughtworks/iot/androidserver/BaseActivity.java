package thoughtworks.iot.androidserver;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BaseActivity extends AppCompatActivity {

    BaseActivity activityReference=this;
    BluetoothThread bluetoothThread;
    HttpServerThread httpServerThread;
    Shelf[] wareHouse;
    int[] availableItemsForsell;
    ArrayList<Cart> oredersToDispatch = new ArrayList<>();
    boolean isDispatchInProgress = false;
    Handler statusHandler;
    //IP
    int portNumberOfServer = 5001;
    private BluetoothDevice bdevice = null;
    TextView statusMessage;
Button dispatchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusMessage=(TextView)findViewById(R.id.statusMessage);
        dispatchButton=(Button)findViewById(R.id.buttonDispature);
        dispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart testcart=new Cart(1,1,1,1);
                oredersToDispatch.add(testcart);
                executeOrderDispatch();
            }
        });
        statusHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String messageFromHandler = msg.getData().getString("message");
                statusMessage.setText(messageFromHandler);
            }
        };
        initializeThread();
        initializeVariables();
    }

    private void initializeThread() {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_baseactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        makeToast(item.toString());
        if (item.getItemId() == R.id.connect_bluetooth) {
            showBluetoothSelectionDialog();
            return true;
        }
        if (item.getItemId() == R.id.connect_tcp) {

        }
        return true;
    }
    private void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    void initializeVariables() {
        availableItemsForsell = new int[4];
        for (int counter = 0; counter < availableItemsForsell.length; counter++) {
            availableItemsForsell[counter] = 4;
        }
        wareHouse = new Shelf[4];
        int[] angleforShelf1 = {82, 84, 86, 88, 90};
        wareHouse[0] = new Shelf(1, 1, 4, angleforShelf1, activityReference);

        int[] angleforShelf2 = {98, 96, 94, 92, 90};
        wareHouse[1] = new Shelf(2, 1, 4, angleforShelf2, activityReference);

        int[] angleforShelf3 = {82, 84, 86, 88, 90};
        wareHouse[2] = new Shelf(3, 2, 4, angleforShelf3,activityReference);

        int[] angleforShelf4 = {98, 96, 94, 92, 90};
        wareHouse[3] = new Shelf(4, 2, 4, angleforShelf4, activityReference);
    }
    private void connectBluetooth() {
        bluetoothThread = new BluetoothThread(bdevice, statusHandler, activityReference);
        bluetoothThread.start();
    }
    void executeOrderDispatch() {
       if(!isDispatchInProgress){
           if(oredersToDispatch.size()>0){
               isDispatchInProgress=true;
               OrderDispatcher orderDispatcher=new OrderDispatcher();
               orderDispatcher.execute();
           }

       }
    }
    private void showBluetoothSelectionDialog() {
        final Dialog deviceList = new Dialog(activityReference);
        //setting custom layout to dialog
        deviceList.setContentView(R.layout.devicelist_dialog);
        deviceList.setTitle("Select Device");
        ListView listView = (ListView) deviceList.findViewById(R.id.lv_devices);

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();

        if (blueAdapter == null) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = blueAdapter.getBondedDevices();
        final ArrayList<BluetoothDevice> devicelist = new ArrayList<>();
        ArrayList<String> devicenamelist = new ArrayList<>();
        devicelist.add(null);
        devicenamelist.add("demo");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                devicelist.add(device);
                devicenamelist.add(device.getName());

            }
        }
        ArrayAdapter adapter = new ArrayAdapter<>(activityReference,
                android.R.layout.simple_list_item_1,
                devicenamelist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                bdevice = devicelist.get(position);
                deviceList.dismiss();
                connectBluetooth();
            }
        });
        deviceList.show();
    }
    private class OrderDispatcher extends AsyncTask<Void,Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            oredersToDispatch.remove(0);
            isDispatchInProgress=false;
            executeOrderDispatch();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cart orderToDispatch=oredersToDispatch.get(0);
            bluetoothThread.changeBeltMotorStatus(0,true);
            for(int counter=0;counter<wareHouse.length;counter++){
                if(orderToDispatch.itemList[counter]>0){
                    wareHouse[counter].dropItemOnBelt(orderToDispatch.itemList[counter]);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bluetoothThread.changeBeltMotorStatus(0,false);
            return null;
        }
    }


}
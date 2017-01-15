package com.hackathone.iotretail.warehouseserver;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BaseActivity extends AppCompatActivity {

    BaseActivity activityReference = this;
    BluetoothThread bluetoothThread;
    HttpServerThread httpServerThread;
    Shelf[] wareHouse;
    ShelfView[] shelfViews=new ShelfView[4];
    int[] availableItemsForsell;
    ArrayList<Cart> oredersToDispatch = new ArrayList<>();
    boolean isDispatchInProgress = false;
    Handler statusHandler;
    //IP
    int portNumberOfServer = 5001;
    private BluetoothDevice bdevice = null;
    TextView statusMessage;
    Button dispatchButton;
    Button resetMotorOne,resetMotorTwo ;
    TrackView trackView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusMessage = (TextView) findViewById(R.id.statusMessage);
        dispatchButton = (Button) findViewById(R.id.buttonDispature);
        dispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart testcart = new Cart(1, 1, 1, 1);
                addCarttoDispatchList(testcart);
            }
        });
        resetMotorOne = (Button) findViewById(R.id.reset_button_one);
        resetMotorTwo = (Button) findViewById(R.id.reset_button_two);
        resetMotorOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothThread.resetMotor(0, 81);
            }
        });
        resetMotorTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothThread.resetMotor(1, 93);
            }
        });
        statusHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String messageFromHandler = msg.getData().getString("message");
                statusMessage.setText(messageFromHandler);
            }
        };
        shelfViews[0]=(ShelfView) findViewById(R.id.shelf1);
        shelfViews[1]=(ShelfView) findViewById(R.id.shelf2);
        shelfViews[2]=(ShelfView) findViewById(R.id.shelf3);
        shelfViews[3]=(ShelfView) findViewById(R.id.shelf4);
        for(int i=0;i<shelfViews.length;i++){
            shelfViews[i].shelfNumber=i+1;
        }
        trackView=(TrackView)findViewById(R.id.trackview);
        initializeThread();
        initializeVariables();
    }

    void addCarttoDispatchList(Cart testcart) {
        oredersToDispatch.add(testcart);
        executeOrderDispatch();
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
            startHttpServer();
        }

        if (item.getItemId() == R.id.settings_warehouse) {
            showWarehouseSettingDialog();
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
        int[] angleforShelf1 = {40, 46, 56, 66};
        wareHouse[0] = new Shelf(1, 0, 4, angleforShelf1, activityReference,shelfViews[0]);

        int[] angleforShelf2 = {121, 115, 105, 95};
        wareHouse[1] = new Shelf(2, 0, 4, angleforShelf2, activityReference,shelfViews[1]);

        int[] angleforShelf3 = {132, 124, 116, 108};
        wareHouse[2] = new Shelf(3, 1, 4, angleforShelf3, activityReference,shelfViews[2]);

        int[] angleforShelf4 = {48, 58, 68, 78};
        wareHouse[3] = new Shelf(4, 1, 4, angleforShelf4, activityReference,shelfViews[3]);
    }

    private void startHttpServer() {
        try {
            new AppServer(activityReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectBluetooth() {
        bluetoothThread = new BluetoothThread(bdevice, statusHandler, activityReference);
        bluetoothThread.start();
    }

    void executeOrderDispatch() {
        if (!isDispatchInProgress) {
            if (oredersToDispatch.size() > 0) {
                isDispatchInProgress = true;
                OrderDispatcher orderDispatcher = new OrderDispatcher();
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


    private void showWarehouseSettingDialog() {
        final Dialog warehouseSetting = new Dialog(activityReference);
        //setting custom layout to dialog
        warehouseSetting.setContentView(R.layout.ware_house_setting_dialog);
        warehouseSetting.setTitle("Warehouse Settings");

        final EditText editTextOne = (EditText) warehouseSetting.findViewById(R.id.editTextShelf1);
        final EditText editTextTwo = (EditText) warehouseSetting.findViewById(R.id.editTextShelf2);
        final EditText editTextThree = (EditText) warehouseSetting.findViewById(R.id.editTextShelf3);
        final EditText editTextFour = (EditText) warehouseSetting.findViewById(R.id.editTextShelf4);
        warehouseSetting.show();

        final SharedPreferences sharedPreferences = activityReference.getSharedPreferences("defaultSettings", MODE_PRIVATE);
        editTextOne.setText(sharedPreferences.getString("shelfOneAngles", "4,40,46,56,66"));
        editTextTwo.setText(sharedPreferences.getString("shelfTwoAngles", "4,121,115,105,95"));
        editTextThree.setText(sharedPreferences.getString("shelfThreeAngles", "4,132,124,116,108"));
        editTextFour.setText(sharedPreferences.getString("shelfFourAngles", "4,48,58,68,78"));


        Button buttonSet = (Button) warehouseSetting.findViewById(R.id.setButtonWarehouse);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String shelfOneNewParameters = editTextOne.getText().toString();
                String[] shelfOneParameterValues = shelfOneNewParameters.split(",");
                if (shelfOneParameterValues.length == 5) {
                    editor.putString("shelfOneAngles", shelfOneNewParameters);
                    int[] parameters = new int[4];
                    for (int counter = 0; counter < 4; counter++) {
                        parameters[counter] = Integer.parseInt(shelfOneParameterValues[counter+1]);
                    }
                    wareHouse[0] = new Shelf(1, 0, Integer.parseInt(shelfOneParameterValues[0]), parameters, activityReference,shelfViews[0]);
                }

                String shelfTwoNewParameters = editTextTwo.getText().toString();
                String[] shelfTwoParameterValues = shelfTwoNewParameters.split(",");
                if (shelfTwoParameterValues.length == 5) {
                    editor.putString("shelfTwoAngles", shelfTwoNewParameters);
                    int[] parameters = new int[4];
                    for (int counter = 0; counter < 4; counter++) {
                        parameters[counter] = Integer.parseInt(shelfTwoParameterValues[counter+1]);
                    }
                    wareHouse[1] = new Shelf(2, 0, Integer.parseInt(shelfTwoParameterValues[0]), parameters, activityReference,shelfViews[0]);
                }

                String shelfThreeNewParameters = editTextThree.getText().toString();
                String[] shelfThreeParameterValues = shelfThreeNewParameters.split(",");
                if (shelfThreeParameterValues.length == 5) {
                    editor.putString("shelfThreeAngles", shelfThreeNewParameters);
                    int[] parameters = new int[4];
                    for (int counter = 0; counter < 4; counter++) {
                        parameters[counter] = Integer.parseInt(shelfThreeParameterValues[counter+1]);
                    }
                    wareHouse[2] = new Shelf(3, 1, Integer.parseInt(shelfThreeParameterValues[0]), parameters, activityReference,shelfViews[0]);
                }

                String shelfFourNewParameters = editTextFour.getText().toString();
                String[] shelfFourParameterValues = shelfFourNewParameters.split(",");
                if (shelfFourParameterValues.length == 5) {
                    editor.putString("shelfFourAngles", shelfFourNewParameters);
                    int[] parameters = new int[4];
                    for (int counter = 0; counter < 4; counter++) {
                        parameters[counter] = Integer.parseInt(shelfFourParameterValues[counter+1]);
                    }
                    wareHouse[3] = new Shelf(4, 1, Integer.parseInt(shelfFourParameterValues[0]), parameters, activityReference,shelfViews[0]);
                }
                editor.commit();
                warehouseSetting.dismiss();
            }
        });

        Button buttonCancel = (Button) warehouseSetting.findViewById(R.id.cancelButtonWarehouse);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warehouseSetting.dismiss();
            }
        });

    }


    private class OrderDispatcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            oredersToDispatch.remove(0);
            isDispatchInProgress = false;
            executeOrderDispatch();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cart orderToDispatch = oredersToDispatch.get(0);
            if(bluetoothThread!=null){
                bluetoothThread.changeBeltMotorStatus(0, true);
            }


            for (int counter = 0; counter < wareHouse.length; counter++) {
                if (orderToDispatch.itemList[counter] > 0) {
                    wareHouse[counter].dropItemOnBelt(orderToDispatch.itemList[counter]);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(bluetoothThread!=null) {
                bluetoothThread.changeBeltMotorStatus(0, false);
            }
            return null;
        }
    }


}

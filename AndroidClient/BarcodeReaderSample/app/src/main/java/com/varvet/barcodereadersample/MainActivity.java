package com.varvet.barcodereadersample;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;
import com.varvet.barcodereadersample.camera.CartItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    ArrayList<CartItem> arrayListCart;
    AdapterProductList adapterProductList;
    SharedPreferences sharedPreferences;
    private ListView listViewCartItem;
    private Button buttonScanBarCode, buttonAdd, buttonCheckOut;
    private EditText editTextBarCode;
    private String SERVER_IP = "10.131.127.6:8080";
    private List<CartItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getApplicationContext().getSharedPreferences("appsettings", MODE_PRIVATE);
        SERVER_IP = sharedPreferences.getString("serverIp", "10.131.127.6");
        editTextBarCode = (EditText) findViewById(R.id.editText_barCode);

        buttonScanBarCode = (Button) findViewById(R.id.scan_barcode_button);
        listViewCartItem = (ListView) findViewById(R.id.listView_item);
        buttonAdd = (Button) findViewById(R.id.add_button);
        buttonCheckOut = (Button) findViewById(R.id.checkout_button);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barCode = editTextBarCode.getText().toString();
                for (CartItem cartItem : itemList) {
                    if (cartItem.getQrCode().equals(barCode)) {
                        CartItem newCartItem = new CartItem(cartItem.getProductName(), 1, barCode);
                        arrayListCart.add(newCartItem);
                        listViewCartItem.invalidate();
                        Toast.makeText(getApplication(), "Item added" + newCartItem.getProductName(), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(getApplication(), "Invalid QRCode", Toast.LENGTH_LONG).show();

            }
        });

        arrayListCart = new ArrayList<>();
        itemList = new ArrayList<>();
        adapterProductList = new AdapterProductList(this, R.layout.cart_item_view, arrayListCart);
        // adapterProductList.add(arrayListCart);
        listViewCartItem.setAdapter(adapterProductList);
        listViewCartItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        buttonScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
        HttpClientGet httpClientGet = new HttpClientGet();
        httpClientGet.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    editTextBarCode.setText(barcode.displayValue);
                } else editTextBarCode.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            showSettingDialog();
            return true;
        }
        return true;
    }

    void showSettingDialog() {
        final Dialog settingDialog = new Dialog(this);
        settingDialog.setContentView(R.layout.setting_dialog);
        settingDialog.show();

        final EditText editTextServerIP = (EditText) settingDialog.findViewById(R.id.editTextServerIp);
        SERVER_IP = sharedPreferences.getString("serverIp", "10.131.127.2");
        editTextServerIP.setText(SERVER_IP);
        final Button buttonSet = (Button) settingDialog.findViewById(R.id.buttonSet);
        final Button buttonCancel = (Button) settingDialog.findViewById(R.id.buttonCancel);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Button clicked");
                SERVER_IP = editTextServerIP.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("serverIp", SERVER_IP);
                editor.commit();
                settingDialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
            }
        });
    }

    public class HttpClientGet extends AsyncTask<String, Void, String> {

        public static final String TAG = "GETHTTP";

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL("http://10.131.127.6:8080"); // here is your URL path
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");


                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Sending get request : " + url);
                Log.d(TAG, "Response code : " + responseCode);


                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String output;
                    StringBuffer response = new StringBuffer();

                    while ((output = in.readLine()) != null) {
                        response.append(output);
                    }
                    in.close();

                    //printing result from response
                    Log.d(TAG, response.toString());
                    String responseJson = response.toString();
                    JSONObject jsonObject = new JSONObject(responseJson);
                    JSONObject json = new JSONObject(responseJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("Products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productJson = jsonArray.getJSONObject(i);
                        String productName = productJson.getString("productName");
                        String productQrCode = productJson.getString("productQrCode");
                        itemList.add(new CartItem(productName, 0, productQrCode));
                    }

                    return response.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
//        baseactivity.showResponse(result);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
        }
    }

    public class HttpClientPost extends AsyncTask<String, Void, String> {
        private MainActivity baseactivity;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... args) {

            try {

                URL url = new URL("http://10.131.127.6:8080"); // here is your URL path


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(args[0]);

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("{ error :   + responseCode }");
                }
            } catch (Exception e) {
                return new String("{ error : + e.getMessage()} ");
            }

        }

        @Override
        protected void onPostExecute(String result) {
//        baseactivity.showResponse(result);
        }
    }
}

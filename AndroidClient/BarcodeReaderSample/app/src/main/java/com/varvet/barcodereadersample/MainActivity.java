package com.varvet.barcodereadersample;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.varvet.barcodereadersample.barcode.BarcodeCaptureActivity;
import com.varvet.barcodereadersample.camera.CartItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private ListView listViewCartItem;
    private Button buttonScanBarCode,buttonAdd,buttonCheckOut;
    private EditText editTextBarCode;
    AdapterProductList adapterProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextBarCode = (EditText) findViewById(R.id.editText_barCode);

        buttonScanBarCode = (Button) findViewById(R.id.scan_barcode_button);
        listViewCartItem =(ListView)findViewById(R.id.listView_item);
        buttonAdd=(Button)findViewById(R.id.add_button);
        buttonCheckOut=(Button)findViewById(R.id.checkout_button);

        ArrayList<CartItem> arrayListCart=new ArrayList<CartItem>();
        adapterProductList=new AdapterProductList(this,R.layout.cart_item_view, arrayListCart);
        adapterProductList.add(arrayListCart);
        listViewCartItem.setAdapter(adapterProductList);
        buttonScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
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
}

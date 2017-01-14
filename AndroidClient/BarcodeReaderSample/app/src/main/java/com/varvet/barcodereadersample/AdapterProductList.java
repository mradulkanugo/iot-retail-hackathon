package com.varvet.barcodereadersample;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.varvet.barcodereadersample.camera.CartItem;

import java.util.ArrayList;


/**
 * Created by amitp on 1/14/17.
 */
public class AdapterProductList extends ArrayAdapter {

    ArrayList<CartItem> cartItems;
    public AdapterProductList(Context context, int resource, ArrayList<CartItem> cartItems) {
        super(context, resource);
        this.cartItems=cartItems;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.cart_item_view, null);

        }
        TextView productName= (TextView) view.findViewById(R.id.productName_textView);
        TextView quantity = (TextView) view.findViewById(R.id.quantity_textView);
        productName.setText(cartItems.get(position).getProductName());
        quantity.setText((cartItems.get(position).getQuantity()));

        return view;
    }
}

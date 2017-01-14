package com.varvet.barcodereadersample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.varvet.barcodereadersample.camera.CartItem;

import java.util.List;


/**
 * Created by amitp on 1/14/17.
 */
public class CartProductAdapter extends ArrayAdapter<CartItem> {

    public CartProductAdapter(Context context, int resource, List<CartItem> cartItems) {
        super(context, resource, cartItems);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cart_item_view, parent, false);
        }
        TextView productName = (TextView) view.findViewById(R.id.productName_textView);
        TextView quantity = (TextView) view.findViewById(R.id.quantity_textView);
        productName.setText(getItem(position).getProductName());
        quantity.setText((String.valueOf(getItem(position).getQuantity())));

        Button removeItemImage = (Button) view.findViewById(R.id.quantity_textView);
        removeItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(getItem(position));
                notifyDataSetChanged();
            }
        });

        return view;
    }
}

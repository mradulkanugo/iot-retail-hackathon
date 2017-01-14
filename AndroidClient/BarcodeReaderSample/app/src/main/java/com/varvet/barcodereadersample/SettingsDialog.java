package com.varvet.barcodereadersample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by onkarshedge on 1/14/17.
 */

public class SettingsDialog extends DialogFragment {

    public static final String TAG = SettingsDialog.class.getSimpleName();
    public static final String PREF_NAME = "appSettings";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View view = layoutInflater.inflate(R.layout.setting_dialog, null);
        EditText editTextServerIP = (EditText) view.findViewById(R.id.editTextServerIp);
        String SERVER_IP = sharedPreferences.getString("serverIp", "10.131.127.2");
        editTextServerIP.setText(SERVER_IP);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Set clicked");
                        EditText editTextServerIP = (EditText) view.findViewById(R.id.editTextServerIp);
                        String SERVER_IP = editTextServerIP.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("serverIp", SERVER_IP);
                        editor.commit();
                    }
                }).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel Clicked");
                    }
                }).create();
        alertDialog.setView(view);
        return alertDialog;
    }
}

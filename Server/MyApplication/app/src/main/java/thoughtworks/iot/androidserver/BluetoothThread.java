package thoughtworks.iot.androidserver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothThread extends Thread{

    public BluetoothSocket mmSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    public boolean torun = true;
    private Handler statusHandler;
    public DataOutputStream streamOut = null;
    private DataInputStream streamIn = null;
    private final BluetoothDevice mmDevice;
    private BaseActivity activity;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothThread(BluetoothDevice device, Handler _status_h, BaseActivity activity) {
        this.statusHandler = _status_h;
        this.mmDevice = device;
        this.activity = activity;

    }

    public void run() {

        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mmSocket.connect();
            mOutputStream = mmSocket.getOutputStream();
            mInputStream = mmSocket.getInputStream();
            streamOut = new DataOutputStream(new BufferedOutputStream(mOutputStream));
            streamIn = new DataInputStream(new BufferedInputStream(mInputStream));
            sendStatusMessage("BL created connection");
        } catch (IOException connectException) {
            sendStatusMessage("BL connection fail");
            close();
            torun = false;
            return;
        }
        //TODO: design pattern
        while (torun) {
            try {

                String jsonString=streamIn.readLine();
                sendStatusMessage(jsonString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sendStatusMessage("BL connection Stopped ");
        close();
        return;
    }

    public void close() {
        torun = false;
        try {
            if (streamOut != null) {
                streamOut.close();
            }
            if (streamIn != null) {
                streamIn.close();
            }
            if (mmSocket != null) {
                mmSocket.close();
            }
        } catch (IOException ioe) {
        }
    }

    public boolean changeBeltMotorStatus(int motorId, boolean status) {
        if (streamOut != null) {

            String messageToBeSent;
            if (status) {
                messageToBeSent = "{\"commandType\":\"start\"";
            } else {
                messageToBeSent = "{\"commandType\":\"stop\"";
            }
            messageToBeSent += ", \"motorId\":" + motorId + ",\"data\": 0}\n";
            try {
                streamOut.writeChars(messageToBeSent);
                streamOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
                close();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean turnMotorByDegrees(int motorId, int degree) {
        if (streamOut != null) {

            String messageToBeSent;
            messageToBeSent = "{\"commandType\" : \"set\", \"motorId\":" + motorId + ",\"data\": " + degree + "}\n";
            try {
                streamOut.writeChars(messageToBeSent);
                streamOut.flush();
                sendStatusMessage(messageToBeSent);
            } catch (IOException e) {
                e.printStackTrace();
                close();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    private void sendStatusMessage(String sr) {
        Message m = statusHandler.obtainMessage();
        Bundle bl = new Bundle();
        bl.putString("message", sr);
        m.setData(bl);
        statusHandler.sendMessage(m);
    }

}

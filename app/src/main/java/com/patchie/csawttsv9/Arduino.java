package com.patchie.csawttsv9;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Czar Art Z. Duran on 21/01/2018.
 */

public class Arduino {
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    Context Lcontext;
    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;

    //Initialization
    public Arduino(Context context) {
        this.Lcontext = context;
        usbManager = (UsbManager) Lcontext.getSystemService(context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        Lcontext.registerReceiver(broadcastReceiver, filter);
    }

    public UsbSerialInterface.UsbReadCallback lCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            String data = null;
            try{
                data = new String(bytes, "UTF-8");
                if (SMSActivity.active){
                    SMSActivity inst = SMSActivity.instance();
                    inst.ArduinoBridge(data);
                }
            }catch (UnsupportedEncodingException e){
                Log.e("Arduino", "error getting data");
            }
        }
    };

    public void Start() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int SetDeviceVID = Integer.valueOf(Lcontext.getString(R.string.VendorID));
                if (deviceVID == SetDeviceVID) {
                    PendingIntent pi = PendingIntent.getBroadcast(Lcontext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }
            }
        } else {
            Log.e("Arduino", "EmptyDeviceList");
        }
    }

    public void Stop() {
        try{
            if (serialPort.open() == true) {
                serialPort.close();
            } else {
                Log.e("Android", "Unable to stop serial!");
            }
        }catch (Exception e){
            Log.e("Czar", "No serial port to stop!");
            Toast.makeText(Lcontext, "No Serial port to close!", Toast.LENGTH_SHORT);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    Log.e("Arduino", "Permission granted to access port");
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            //Set Serial Connection Parameters.
                            //setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(lCallback);
                            Toast.makeText(Lcontext, "Serial port active!", Toast.LENGTH_SHORT);
                            //tvAppend(textView, "Serial Connection Opened!\n");
                        } else {
                            Log.e("Czar SERIAL", "PORT NOT OPEN");
                            Toast.makeText(Lcontext, "Serial port inactive", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Log.e("Czar SERIAL", "PORT IS NULL");
                        Toast.makeText(Lcontext, "Invalid serial port", Toast.LENGTH_SHORT);
                    }
                } else {
                    Log.e("Czar SERIAL", "PERMISSION NOT GRANTED");
                    Toast.makeText(Lcontext, "Permission not granted", Toast.LENGTH_SHORT);
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                //onClickStart(startButton);
                Start();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //onClickStop(stopButton);
                Stop();
            }
        }
    };
}

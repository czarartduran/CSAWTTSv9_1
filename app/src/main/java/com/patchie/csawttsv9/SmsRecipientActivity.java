package com.patchie.csawttsv9;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmsRecipientActivity extends AppCompatActivity {

    ArduinoInputConverter aic;
    CSB csb;
    Speaker _speak;
    private int selectedIndex = -1;
    private String SELECTED_NAME, SELECTED_NUMBER;

    ListView contact_lv;
    ArrayList<String> contactlist;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_recipient);
        setTitle(getString(R.string.SmsRecipientActivity));

        aic = new ArduinoInputConverter(getApplicationContext());
        _speak = new Speaker(getApplicationContext());

        contact_lv = findViewById(R.id.call_contact_lv);
        if (contactlist == null){
            Log.e("Czar", "Initialized contact list");
            csb = new CSB(this);
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, csb.CONTACTLIST());
        }
        contact_lv.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _speak = new Speaker(getApplicationContext());
        Speak("Please select contact");

    }

    @Override
    protected void onPause() {

        super.onPause();

        StopScanner();
        UnRegisterIntent();
    }

    @Override
    protected void onResume() {

        super.onResume();

        RegisterIntent();
        StartScanner();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        aic = null;
    }

    private void Speak(String string){
        _speak.speak(string);
    }

    public void sra_prev_btn_OnClickEvent(View view) {
        sra_prev_btn();
    }

    private void sra_prev_btn(){
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= 0 && selectedIndex < lv_count){
            if (selectedIndex >=0 && selectedIndex -1 >= 0){
                selectedIndex--;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }else {
                selectedIndex = 0;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_sel_btn_OnClickEvent(View view) {
        sra_sel_btn();
    }

    private void sra_sel_btn(){
        //Log.e("Czar", "RecipientName: " + csb.RecipientName(selectedIndex));
        SELECTED_NAME = csb.RecipientName(selectedIndex);
        SELECTED_NUMBER = csb.RecipientNumber(selectedIndex);
        Intent intent = new Intent();
        intent.putExtra("CONTACT_NAME", SELECTED_NAME);
        intent.putExtra("CONTACT_NUMBER", SELECTED_NUMBER);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void sra_next_btn_OnClickEvent(View view) {
        sra_next_btn();
    }

    private void sra_next_btn(){
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= -1 && selectedIndex < lv_count){
            if (selectedIndex < lv_count && selectedIndex +1 < lv_count){
                selectedIndex++;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }else {
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_can_btn_OnClickEvent(View view) {
        sra_can_btn();
    }

    private void sra_can_btn(){
        finish();
    }

    /*
    * ARDUINO GLOBAL VARIABLE
    * */
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {
            Log.e("Czar", "Called: onReceivedData");
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                final String input = data;
                Log.e("Received", input);
                //Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT);
                int x = aic.getDecimal(input);

                if (x == aic.CONTROL_OK()){
                    sra_sel_btn();
                }
                if(x == aic.CONTROL_PREVIOUS()){
                    sra_prev_btn();
                }
                if (x == aic.CONTROL_NEXT()){
                    sra_next_btn();
                }
                if (x == aic.CONTROL_CANCEL()){
                    sra_can_btn();
                }


            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                Log.e("Czar", e.getLocalizedMessage());
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Czar", "Called: BroadcastReceiver");
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) {
                            //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            Log.e("Czar", "SerialPort Opened!");

                        } else {
                            Log.e("Czar SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.e("Czar SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.e("Czar SERIAL", "PERMISSION NOT GRANTED");
                }
            }
        }
    };

    private void StartScanner() {
        Log.e("Czar", "StartScanner");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                //if (deviceVID == 0x2341)//Arduino Vendor ID
                if (deviceVID == Integer.valueOf(getString(R.string.VendorID)))//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep) {
                    break;
                }
            }
        } else {
            Log.e("Czar", "No Usb Devices!");
        }
    }

    private void StopScanner() {
        try {
            if (serialPort.open() == true) {
                serialPort.close();
                Log.e("Czar", "SerialPort is Closed!");
            }
        } catch (Exception e) {
            Log.e("Czar", "No serial port to close");
        }
    }

    private void RegisterIntent() {
        Log.e("SmsActivity", "Registering instent");
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    private void UnRegisterIntent() {
        Log.e("SmsActivity", "UnRegistering Intent");
        unregisterReceiver(broadcastReceiver);
    }
}

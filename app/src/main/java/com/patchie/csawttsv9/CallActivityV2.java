package com.patchie.csawttsv9;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.speech.tts.TextToSpeech;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallActivityV2 extends AppCompatActivity {


    private static CallActivityV2 inst;
    public static boolean active = false;

    CSB csb;
    //Speaker _speak;
    Speaker speaker;
    private int selectedIndex = -1;
    private String SELECTED_NAME, SELECTED_NUMBER;

    ListView contact_lv;
    ArrayList<String> contactlist;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_v2);
        setTitle(getString(R.string.CallerActivity));

        //_speak = new Speaker(getApplicationContext());
        speaker = new Speaker(getApplicationContext(), "Welcome to Call Module, On this module you can browse your list of contacts. Press A to go to the next contact, Press B to go to the previous contact, Press @ to select your desire contact, Press D to Dial an unknown number, Press A to add a new contact and Press C to cancel and go to the previous module");

        contact_lv = findViewById(R.id.call_contact_lv);
        if (contactlist == null) {
            Log.e("Czar", "Initialized contact list");
            csb = new CSB(this);
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, csb.CONTACTLIST());
        }
        contact_lv.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        Log.e("CallActivityV2","onPause");

        StopScanner();
        unregisterReceiver(broadcastReceiver);
        super.onPause();


        speaker.destroy();
    }

    @Override
    protected void onResume() {
        Log.e("CallActivityV2", "onResume");

        super.onResume();
        RegisterIntent();
        StartScanner();
        speaker = new Speaker(getApplicationContext());
        speaker.speakAdd("Please select contact");
    }

    @Override
    protected void onStop() {
        Log.e("CallActivityV2", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.e("CallActivityV2", "onDestroy");
        super.onDestroy();
    }
    private void RegisterIntent() {
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }


    public void sra_prev_btn_OnClickEvent(View view) {
        sra_prev_btn();
    }

    private void sra_prev_btn() {
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= 0 && selectedIndex < lv_count) {
            if (selectedIndex >= 0 && selectedIndex - 1 >= 0) {
                selectedIndex--;
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            } else {
                selectedIndex = 0;
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_sel_btn_OnClickEvent(View view) {
        sra_sel_btn();
    }

    private void sra_sel_btn() {
        //Log.e("Czar", "RecipientName: " + csb.RecipientName(selectedIndex));
        /*SELECTED_NAME = csb.RecipientName(selectedIndex);
        SELECTED_NUMBER = csb.RecipientNumber(selectedIndex);
        Intent intent = new Intent();
        intent.putExtra("CONTACT_NAME", SELECTED_NAME);
        intent.putExtra("CONTACT_NUMBER", SELECTED_NUMBER);
        setResult(RESULT_OK, intent);*/
        //finish();

        SELECTED_NUMBER = csb.RecipientNumber(selectedIndex);
        Intent callIntent;
        callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + SELECTED_NUMBER));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    public void sra_next_btn_OnClickEvent(View view) {
        sra_next_btn();
    }

    private void sra_next_btn(){
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= -1 && selectedIndex < lv_count){
            if (selectedIndex < lv_count && selectedIndex +1 < lv_count){
                selectedIndex++;
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }else {
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_can_btn_OnClickEvent(View view) {
        sra_can_btn();
    }

    private void sra_can_btn(){
        speaker.speak("Canceled");
        finish();
    }

    public void call_dial_btn_OnClickEvent(View view) {
        speaker.speak("Dialer module has been clicked");
        Call_Dial_btn();
    }

    Intent DialIntent;
    public void Call_Dial_btn(){
        if (DialIntent == null){
            //DialIntent = new Intent(CallActivityV2.this, Dial_activity.class);
            DialIntent = new Intent(CallActivityV2.this, DialerActivity.class);
        }
        startActivity(DialIntent);
    }

    public void add_contacts_btn_OnclickEvent (View view) {
        speaker.speak("Add contact module has been clicked");
        add_contacts_btn();
    }

    Intent AddContactIntent;
    public void add_contacts_btn(){
        if (AddContactIntent == null){
            //AddContactIntent = new Intent(CallActivityV2.this, Add_contact.class);
            AddContactIntent = new Intent(CallActivityV2.this, Add_contact.class);
        }
        startActivity(AddContactIntent);
    }
//arduino input codes
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            Log.e("jibeh", "Called: onReceivedData");
            String data = null;

            try {
                data = new String(bytes,"UTF-8");
                final String input = data;
                ArduinoInputConverter aic = new ArduinoInputConverter(getApplicationContext());

                int j = aic.getDecimal(input);
                if (j == aic.CONTROL_NEXT_CALL()){
                    sra_next_btn();
                }
                if(j == aic.CONTROL_PREV_CALL()){
                    sra_prev_btn();
                }
                if(j == aic.CONTROL_DIALER()){
                    Call_Dial_btn();
                }
                if(j == aic.CONTROL_ADD_CONTACT()){
                    add_contacts_btn();
                }
                if(j == aic.CONTROL_SELECT_CALL()){
                    sra_sel_btn();
                }
                if(j == aic.CONTROL_CANCEL()){
                    sra_can_btn();
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("jibeh", "Called: BroadcastReceiver");
            if(intent.getAction().equals(ACTION_USB_PERMISSION)){
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if(granted){
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if(serialPort !=null){
                        if(serialPort.open()){
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            Log.e("jibeh", "SerialPort Opened!");

                        }
                        else {
                            Log.e("jibeh SERIAL", "PORT NOT OPEN");
                        }
                    }
                    else {
                        Log.e("jibeh SERIAL", "PORT IS NULL");
                    }
                }
                else     {
                    Log.e("jibeh SERIAL", "PERMISSION NOT GRANTED");
                }
            }
        }
    };
    private void StartScanner(){
        Log.e("MainActivity", "Starting SerialPort");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if(!usbDevices.isEmpty()){
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
                    Log.e("MainActivity", "Successfully set device serial port");
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep) {
                    break;
                }
            }
        }
        else {
            Log.e("MainActivity", "No Usb Devices!");
        }
    }
    private void StopScanner() {
        Log.e("MainActivity", "Stopping SerialPort");
        try {
            if (serialPort.open() == true) {
                serialPort.close();
                Log.e("MainActivity", "SerialPort is Closed!");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "No serial port to close");
        }
    }

    }



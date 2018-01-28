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

    private boolean isFirstLoad = true;

    private static CallActivityV2 inst;
    public static boolean active = false;

    CSB csb = null;
    Speaker speaker;
    private int selectedIndex = -1;
    private String SELECTED_NAME, SELECTED_NUMBER;

    ListView contact_lv;
    ArrayList<String> contactlist;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("CallActivityV2", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_v2);
        setTitle(getString(R.string.CallerActivity));

        speaker = new Speaker(getApplicationContext(), getString(R.string.callWelcomeMessage));
        isFirstLoad = false;

        contact_lv = findViewById(R.id.call_contact_lv);
        if (contactlist == null) {
            Log.e("Czar", "Initialized contact list");
            csb = new CSB(getApplicationContext());
            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, csb.CONTACTLIST());
        }
        contact_lv.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        Log.e("CallActivityV2", "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.e("CallActivityV2", "onPause");
        super.onPause();



        StopScanner();
        UnRegisterArduinoIntent();

        if (speaker.isSpeaking()) {
            Log.e("MainActivity: onPause", "Stopping speaker");
            speaker.stop();
        }
    }

    @Override
    protected void onResume() {
        Log.e("CallActivityV2", "onResume");
        super.onResume();

        if (speaker == null) {
            Log.e("CallActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }

        RegisterArduinoIntent();
        StartScanner();

        if (!isFirstLoad) {
            speaker.speak(getString(R.string.callWelcomeMessage));
        }
    }

    @Override
    protected void onStop() {
        Log.e("CallActivityV2", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("CallActivityV2", "onDestroy");
        super.onDestroy();

        speaker.destroy();
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

    private void sra_next_btn() {
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= -1 && selectedIndex < lv_count) {
            if (selectedIndex < lv_count && selectedIndex + 1 < lv_count) {
                selectedIndex++;
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            } else {
                //Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
                speaker.speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_can_btn_OnClickEvent(View view) {
        sra_can_btn();
    }

    private void sra_can_btn() {
        speaker.speak("Canceled");
        finish();
    }

    public void call_dial_btn_OnClickEvent(View view) {
        speaker.speak("Dialer module has been clicked");
        Call_Dial_btn();
    }


    public void Call_Dial_btn() {
        Intent DialIntent =null;
        if (DialIntent == null) {
            //DialIntent = new Intent(CallActivityV2.this, Dial_activity.class);
            DialIntent = new Intent(CallActivityV2.this, DialerActivity.class);
        }
        startActivity(DialIntent);
    }

    public void add_contacts_btn_OnclickEvent(View view) {
        speaker.speak("Add contact module has been clicked");
        add_contacts_btn();
    }


    public void add_contacts_btn() {
        Intent AddContactIntent = null;
        if (AddContactIntent == null) {
            //AddContactIntent = new Intent(CallActivityV2.this, Add_contact.class);
            AddContactIntent = new Intent(CallActivityV2.this, Add_contact.class);
        }
        startActivity(AddContactIntent);
    }

    /*
    * ARDUINO GLOBAL VARIABLE
    * */
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

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

    private void RegisterArduinoIntent() {
        Log.e("SmsActivity", "Registering instent");
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    private void UnRegisterArduinoIntent() {
        Log.e("SmsActivity", "UnRegistering Intent");
        unregisterReceiver(broadcastReceiver);
    }

    private void CallBack(int x) {
        ArduinoInputConverter aic = new ArduinoInputConverter(getApplicationContext());
        if (x == aic.CONTROL_PREVIOUS()) {
            //aic = null;
            sra_prev_btn();
        }
        if (x == aic.CONTROL_OK()) {
            //aic = null;
            sra_sel_btn();
        }
        if (x == aic.CONTROL_NEXT()) {
            //aic = null;
            sra_next_btn();
        }
        if (x == aic.CONTROL_COMPOSE()) {
            //aic = null;
            add_contacts_btn();
        }
        if (x == aic.CONTROL_CANCEL()) {
            //aic = null;
            sra_can_btn();
        }
        if (x == aic.CONTROL_REPLY()) {
            //aic = null;
            Call_Dial_btn();
        }
    }

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {
            Log.e("Czar", "Called: onReceivedData");
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                final String input = data;
                ArduinoInputConverter aic = new ArduinoInputConverter(getApplicationContext());

                int x = aic.getDecimal(input);
                aic = null;
                CallBack(x);

                /*if (x == aic.CONTROL_PREVIOUS()) {
                    PreviousMessage();
                }
                if (x == aic.CONTROL_NEXT()) {
                    NextMessage();
                }
                if (x == aic.CONTROL_COMPOSE()) {
                    CallComposeActivity();
                }
                if (x == aic.CONTROL_REPLY()) {
                    replyButtonOnClickEvent();
                }*/
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
}



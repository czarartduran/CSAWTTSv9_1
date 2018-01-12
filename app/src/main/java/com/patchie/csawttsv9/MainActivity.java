package com.patchie.csawttsv9;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    EditText editText;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {
            Log.e("Czar", "Called: onReceivedData");
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                final String input = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.setText(input);
                    }
                });



                /*switch (data) {
                    case "1":

                        *//**tvAppend(textView, "A");**//*
                        *//**clearButton = (Button) findViewById(R.id.buttonClear);**//*
                        tvAppend(editText, "A");
                        break;
                    case "2":
                        tvAppend(textView, "B");
                        break;
                }*/
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                editText.setText(e.toString());
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
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            //setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            //tvAppend(textView, "Serial Connection Opened!\n");

                        } else {
                            Log.e("Czar SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.e("Czar SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.e("Czar SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                //onClickStart(startButton);
                StartScanner();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //onClickStop(stopButton);
                StopScanner();
            }
        }

        ;
    };

    private void StartScanner(){
        Log.e("Czar", "onClickerStart");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                //if (deviceVID == 0x2341)//Arduino Vendor ID
                if (deviceVID == 0x067B)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    private void StopScanner(){
        Log.e("Czar", "onClickStart");
        //setUiEnabled(false);

        if (serialPort.open() == true)
        {
            serialPort.close();
        }
        //tvAppend(textView, "\nSerial Connection Closed! \n");
    }

    Speaker _speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Maxing Volumes
        SetVolumes();

        //initializing speaker
        if (_speaker == null){
            _speaker = new Speaker(getApplicationContext());
            Log.e("Czar","_speaker has been initialized");
        }

        //assigning editText2
        editText = (EditText) findViewById(R.id.editText2);
        Log.e("Czar", "editText has been initialized");

        //initializing arduino scanner
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        Log.e("Czar", "Arduino has been initialized");

        //StartScanner();
    }

    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();

        //Speak("Pasok mga suki, presyong divisoria, MAY SAMPU SAMPU");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        _speaker.destroy();
        //StopScanner();
    }

    private void Speak(String TextToRead) {
        _speaker.speak(TextToRead);
    }

    private void CallActivity(){
        startActivity(new Intent(MainActivity.this, Contact_list.class));
    }

    protected void SmsActivity(){
        startActivity(new Intent(MainActivity.this, SMSActivity.class));
    }

    //This is function to max volumes
    private void SetVolumes(){
        AudioManager am = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC , 100, 0);
        am.setStreamVolume(AudioManager.STREAM_ALARM, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_RING, 100, 0);
    }

    public void sms_btn_OnClickEvent(View view) {
        SmsActivity();
    }

    public void call_btn_OnClick_Event(View view) {
        CallActivity();
    }
}

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
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private void StartScanner() {
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

    private void StopScanner() {
        Log.e("Czar", "onClickStart");
        //setUiEnabled(false);

        if (serialPort.open() == true) {
            serialPort.close();
        }
        //tvAppend(textView, "\nSerial Connection Closed! \n");
    }

    //MainActivity
    Speaker _speaker;
    ArrayList<String> _SMSlist;
    CSB csb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Czar", "MainActivity: OnCreate");

        //Checking Permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions()) {
                //done permission
            }
        }

        //CSB initialization
        if (csb == null) {
            csb = new CSB(this);
            Log.e("Czar", "CSB initiated");
        }

        //Maxing Volumes
        SetVolumes();

        //initializing speaker
        if (_speaker == null) {
            _speaker = new Speaker(getApplicationContext());
            Log.e("Czar", "_speaker has been initialized");
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

    public static final int PERMISSIONS_REQUEST = 1;

    private boolean checkAndRequestPermissions() {
        int permissionCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionModifyContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionReadSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permisionSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionReceived = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        List<String> listPermissionNeeded = new ArrayList<>();
        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissionModifyContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.WRITE_CONTACTS);
        }
        if (permissionReadSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permisionSendSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionReceived != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                Log.e("Czar", "GrantResult: " + grantResults.length);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                if (grantResults.length > 0 && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                if (grantResults.length > 0 && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                if (grantResults.length > 0 && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        Log.e("Czar", "MainActivity: onStart");
        //Testing
        if (csb == null) {
            csb = new CSB(this);
            Log.e("Czar", "CSB initiated");
        }

        // TODO Auto-generated method stub
        super.onStart();

        //Speak("Pasok mga suki, presyong divisoria, MAY SAMPU SAMPU");
    }

    @Override
    protected void onStop() {
        Log.e("Czar", "MainActivity: onStop");

        // TODO Auto-generated method stub
        super.onStop();

        _speaker.destroy();
        //StopScanner();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

    private void Speak(String TextToRead) {
        _speaker.speak(TextToRead);
    }

    private ArrayList<String> messageList;

    private void CallActivity() {
        startActivity(new Intent(MainActivity.this, CallActivity.class));
    }

    Intent SmsIntent;
    protected void SmsActivity() {
        Toast.makeText(this, "Please wait", Toast.LENGTH_LONG);
        if (SmsIntent == null) {
            SmsIntent = new Intent(getApplicationContext(), SMSActivity.class);
        }
        SmsIntent.putStringArrayListExtra("SMSLIST", csb.SMSLIST());
        startActivity(SmsIntent);
    }

    //This is function to max volumes
    private void SetVolumes() {
        AudioManager am = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
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

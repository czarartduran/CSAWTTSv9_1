package com.patchie.csawttsv9;

import android.Manifest;
import android.app.NotificationManager;
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
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //MainActivity
    ArduinoInputConverter aic;
    private Speaker speaker;

    private ArrayList<String> _SMSlist;
    private CSB csb;
    private boolean IS_DONOTDISTURBDISABLE = false;
    private NotificationManager mNotificationManager;
    public static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.MainActivity));
        Log.e("Czar", "MainActivity: OnCreate");

        aic = new ArduinoInputConverter(this);

        //setting it on active
        active = true;

        //Checking Permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAndRequestPermissions()) {
                return;
            } else {
                Log.e("Czar", "MainActivity: Permission granted!");
            }
        }

        //Turning OFF DoNotDisturbMode
        //turnOffDoNotDisturbMode();
        //Maxing out all volume module component
        //SetVolumes();

        //Initializing CSB class
        csb = new CSB(this);

        //initializing speaker
        speaker = new Speaker(getApplicationContext(), getString(R.string.WelcomeMessage));
    }

    @Override
    protected void onStart() {
        Log.e("Czar", "MainActivity: onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Czar", "MainActivity: onResume");

        if (speaker == null){
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }
        RegisterIntent();
        StartScanner();
    }

    @Override
    protected void onPause() {
        Log.e("Czar", "MainActivity: onPause");

        aic = null;
        if (speaker.isSpeaking()){
            Log.e("MainActivity: onPause", "Stopping speaker");
            speaker.stop();
        }
        StopScanner();
        unregisterReceiver(broadcastReceiver);

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("Czar", "MainActivity: onStop");
        super.onStop();

        active = false;

    }

    @Override
    protected void onDestroy() {
        Log.e("Czar", "MainActivity: onDestroy");
        super.onDestroy();

        speaker.destroy();
    }

    private void RegisterIntent() {
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    //main codes
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    //EditText editText;


    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {
            Log.e("Czar", "Called: onReceivedData");
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                final String input = data;

                if (aic.getDecimal(input) == aic.CONTROL_CALL_ACTIVITY()) {
                    CallActivity();
                }
                if (aic.getDecimal(input) == aic.CONTROL_SMS_ACTIVITY()) {
                    SmsActivity();
                }
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                //editText.setText(e.toString());
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
            /*else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                //onClickStart(startButton);
                StartScanner();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                //onClickStop(stopButton);
                StopScanner();
            }*/
        }
    };

    private void StartScanner() {
        Log.e("MainActivity", "Starting SerialPort");
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
                    Log.e("MainActivity", "Successfully set device serial port");
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep) {
                    break;
                }
            }
        } else {
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

    public static final int PERMISSIONS_REQUEST = 1;

    private boolean checkAndRequestPermissions() {
        int permissionCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionModifyContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionReadSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permisionSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionReceived = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permissionNotification = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY);

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
        if (permissionNotification != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
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
                if (grantResults.length > 0 && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    private ArrayList<String> messageList;

    private void CallActivity() {
        //startActivity(new Intent(MainActivity.this, CallActivity.class));
        callActivity();
    }

    Intent CallIntent;

    private void callActivity() {
        if (CallIntent == null) {
            //initialized
            CallIntent = new Intent(getApplicationContext(), CallActivityV2.class);
        }
        /*put extras*/


        startActivity(CallIntent);
    }

    Intent SmsIntent;

    protected void SmsActivity() {
        //Toast.makeText(this, "Please wait", Toast.LENGTH_LONG);
        if (SmsIntent == null) {
            //initialized
            SmsIntent = new Intent(getApplicationContext(), SMSActivity.class);
        }
        /*put extras*/
        SmsIntent.putStringArrayListExtra("SMSLIST", csb.SMSLIST());


        startActivity(SmsIntent);
    }

    // Custom method to turn off do not disturb mode programmatically
    protected void turnOffDoNotDisturbMode() {
        Log.e("Czar", "MainActivity: turnOffDoNotDisturbMode");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                //removing dnd
                mNotificationManager.setInterruptionFilter(mNotificationManager.INTERRUPTION_FILTER_ALL);
                IS_DONOTDISTURBDISABLE = true;
                // Show a toast
                Toast.makeText(this, "Turn OFF Do Not Disturb Mode", Toast.LENGTH_SHORT).show();
                Log.e("Czar", "MainActivity: Successfully turnOffDoNotDisturbMode");
                /*if (_speaker != null){
                    Speak("Do Not Disturb has been turned OFF");
                }else {
                    _speaker = new Speaker(this);
                }*/
            } else {
                Log.e("Czar", "MainActivity: turnOffDoNotDisturbMode NEED PERMISSION");
                Toast.makeText(this, "Going to get grant access", Toast.LENGTH_SHORT).show();
                // If notification policy access not granted for this package
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    //This is function to max volumes
    private void SetVolumes() {
        Log.e("Czar", "IS_DONOTDISTURBDISABLE: " + String.valueOf(IS_DONOTDISTURBDISABLE));
        if (IS_DONOTDISTURBDISABLE == true) {
            AudioManager am = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, 0);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
            am.setStreamVolume(AudioManager.STREAM_ALARM, 100, 0);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 100, 0);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 100, 0);
            am.setStreamVolume(AudioManager.STREAM_RING, 100, 0);
        } else {
            //vibrate if codes fail to enable sound function of the device
            Log.e("Czar", "MainActivity: SOMETHING WENT WRONG SetVolumes");
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(5000);
        }
    }

    public void sms_btn_OnClickEvent(View view) {
        SmsActivity();
    }

    public void call_btn_OnClick_Event(View view) {
        CallActivity();
    }
}

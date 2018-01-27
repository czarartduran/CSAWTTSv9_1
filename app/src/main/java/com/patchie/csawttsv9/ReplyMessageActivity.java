package com.patchie.csawttsv9;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ReplyMessageActivity extends AppCompatActivity {
    /*
    * ARDUINO GLOBAL VARIABLE
    * */
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;


    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    SmsManager smsManager = SmsManager.getDefault();

    Speaker speaker;

    private EditText ReplySmsBody;
    private TextView receiver_tv;
    private String _contactName;
    private String _contactNumber;

    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    PendingIntent sentPI, deliveredPI;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);
        setTitle(getString(R.string.ReplyActivity));

        //initializing speaker
        speaker = new Speaker(getApplicationContext(), getString(R.string.ReplyWelcomeMessage));

        Intent intent = getIntent();
        _contactName = intent.getStringExtra("contactName");
        _contactNumber = intent.getStringExtra("contactNumber");

        receiver_tv = (TextView) findViewById(R.id.receiver_tv);
        receiver_tv.setText(_contactName);

        ReplySmsBody = (EditText) findViewById(R.id.editText);
        ReplySmsBody.setShowSoftInputOnFocus(true);
        ReplySmsBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                speaker.speak(ReplySmsBody.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    }

    @Override
    protected void onStart() {
        Log.e("ReplySmsActivity", "onStart");
        super.onStart();

        RegisterSmsIntent();
    }

    private void RegisterSmsIntent() {
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    //Everything is fine
                    case Activity.RESULT_OK:
                        Log.e("ReplySmsActivity", "Sms Sent successfully");
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_OK));
                        finish();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.e("ReplySmsActivity", "Generic failure");
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_ERROR_GENERICFAILURE));
                        finish();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.e("ReplySmsActivity", "No Service");
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_ERROR_NO_SERVICE));
                        finish();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.e("ReplySmsActivity", "Null PDU");
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_NULL_PDU));
                        finish();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.e("ReplySmsActivity", "Radio OFF");
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_RADIO_OFF));
                        finish();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered!", Toast.LENGTH_SHORT).show();
                        Log.e("ReplySmsActivity", "Sms Delivered");
                        speaker.speak(getString(R.string.DeliverOnReceived_ResultOK));
                        finish();
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.e("ReplySmsActivity", "Sms Not Delivered");
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.DeliverOnReceived_ResultCancel));
                        finish();
                        break;
                }
            }
        };

        //register the BroadCastReceivers to listen for a specific broadcast
        //if they "hear" that broadcast, it will activate their onReceive() method
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));

        Log.e("ReplyActivity", "SmsIntent Registered");
    }

    private void UnRegisterSmsIntent() {
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    @Override
    protected void onPause() {
        Log.e("Czar", "onPause");
        super.onPause();

        if (speaker != null){
            speaker.stop();
            speaker.destroy();
        }

        StopScanner();
        UnRegisterIntent();

        UnRegisterSmsIntent();
    }

    @Override
    protected void onResume() {
        Log.e("ReplySmsActivity", "onResume");
        super.onResume();

        RegisterUsbIntent();
        StartScanner();

        speaker = new Speaker(getApplicationContext());
    }

    @Override
    protected void onStop() {
        Log.e("ReplySmsActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("ReplySmsActivity", "onDestroy");
        super.onDestroy();

        smsManager = null;

        if (speaker != null){
            speaker.stop();
            speaker.destroy();
        }
    }

    public void ReplySMS_Back_btn_OnClick_Event(View view) {
        finish();
    }

    public void ReplySMS_SEND_btn_OnClick_Event(View view) {
        ReplySMS();

        //Log.e("AIC", aic.getChar(ReplySmsBody.getText().toString()));

    }

    private void ReplySMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            try {
                smsManager.sendTextMessage(_contactNumber, null, ReplySmsBody.getText().toString(), sentPI, deliveredPI);
                //Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);

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
                Log.e("Received", input);
                //Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT);
                ArduinoInputConverter aic = new ArduinoInputConverter();

                if (aic.IsForMessaging(input)) {
                    speaker.speak(input);
                    AppendBody(aic.getChar(input));
                } else if (aic.IsSame(input, 64)) {
                    Log.e("ReplyMessageActivity", "Calling BackSpace");
                    BackSpaceBody();
                } else if (aic.IsSame(input, 71)) {
                    finish();
                } else if (aic.IsSame(input, 184)) {
                    ReplySMS();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //checker


                    }
                });
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                Log.e("Czar", e.getLocalizedMessage());
            }
        }
    };

    private void AppendBody(String text) {
        final String input = text;
        speaker.speak(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ReplySmsBody.append(input);
            }
        });

    }

    private void BackSpaceBody() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String old = ReplySmsBody.getText().toString();
                String newStr = "";
                //tb
                ReplySmsBody.setText("");
                if (old.length() > 0) {
                    newStr = old.substring(0, old.length() - 1);
                    ReplySmsBody.append(newStr);
                } else {
                    ReplySmsBody.setText("");
                }
            }
        });
    }

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
        Log.e("Czar", "onClickerStart");
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

    private void RegisterUsbIntent() {
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

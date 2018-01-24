package com.patchie.csawttsv9;

import android.app.Activity;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ComposeMessageActivity extends AppCompatActivity {
    SmsManager smsManager = SmsManager.getDefault();
    private final static int CREATE_REQUEST_CODE = 0130;

    Speaker speaker;

    Button buttonSend;
    Button buttonCancel;
    EditText textPhoneNo;
    private boolean textPhoneOnFocus = true;
    EditText textSMS;
    private boolean textSMSOnFocus = false;

    String conname = "";
    String connum = null;

    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ComposeMessageActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        setTitle(getString(R.string.ComposeActivity));

        speaker = new Speaker(this, getString(R.string.ComposedWelcome));

        //To disable clickable
        /*EditText compose = (EditText)findViewById(R.id.compose);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);

        contactnumber.setShowSoftInputOnFocus(false);
        compose.setShowSoftInputOnFocus(false);*/


        textPhoneNo = (EditText) findViewById(R.id.contactnumber);
        textSMS = (EditText) findViewById(R.id.compose);
        /*buttonCancel = (Button) findViewById(R.id.buttonCancel);*/

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    }

    @Override
    protected void onStart() {

        super.onStart();

        //Must instruct base on the current onFoccus object
        //speaker = new Speaker(getApplicationContext());
        if (textPhoneOnFocus == true) {
            Log.e("Czar", "PhoneOnFocus");
            speaker.speakAdd(getString(R.string.SelectedPhoneInstruction));
        } else {
            Log.e("Czar", "TextOnFocus");
            speaker.speakAdd(getString(R.string.CreatingTextInstruction));
        }
        Log.e("ComposeMessageActivity", "onStart");
    }

    @Override
    protected void onPause() {
        Log.e("ComposeMessageActivity", "onPause");
        super.onPause();

        speaker.stop();
        UnRegisterIntents();

        //Arduino
        UnRegisterIntent();
        StopScanner();
    }

    @Override
    protected void onResume() {
        Log.e("ComposeMessageActivity", "onResume");
        super.onResume();

        if (speaker == null) {
            speaker = new Speaker(this);
        }

        RegisterIntents();

        //Arduino
        RegisterIntent();
        StartScanner();

        //checking which object to focus
        if (textPhoneOnFocus) {
            textPhoneNo.setFocusable(true);
            textSMS.setFocusable(false);
        } else {
            textPhoneNo.setFocusable(false);
            textSMS.setFocusable(true);
        }
    }

    @Override
    protected void onStop() {
        Log.e("ComposeMessageActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("ComposeMessageActivity", "onDestroy");
        super.onDestroy();

        //UnRegisterIntents();
        //finish();
    }

    private void cancelComposeButton() {
        finish();
    }

    public void CancelBtn_OnClick_Event(View view) {
        cancelComposeButton();
    }

    private void SendMessage() {
        String finalrec = "";
        String phoneNo = textPhoneNo.getText().toString();
        String sms = textSMS.getText().toString();

        if (textPhoneNo.isEnabled() == true) {
            finalrec = textPhoneNo.getText().toString();
        } else {
            finalrec = connum;
        }

        /*if (connum == null){
            finalrec = phoneNo;
        }else {
            finalrec = connum;
        }*/

        try {
            smsManager.sendTextMessage(finalrec, null, sms, sentPI, deliveredPI);
            /*Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_SHORT).show();*/
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void ComposeSMS_send_btn_OnClick_Event(View view) {
        SendMessage();
    }

    public void searchRecipient_btn_OnClickEvent(View view) {
        SearchRecipient_btn_OnClickEvent();
    }

    Intent RecipientIntent;

    private void SearchRecipient_btn_OnClickEvent() {
        if (RecipientIntent == null) {
            RecipientIntent = new Intent(ComposeMessageActivity.this, SmsRecipientActivity.class);
        }
        startActivityForResult(RecipientIntent, CREATE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                conname = data.getStringExtra("CONTACT_NAME");
                connum = data.getStringExtra("CONTACT_NUMBER");
                textPhoneNo.setEnabled(false);
                textPhoneOnFocus = false;
                textSMSOnFocus = true;
                textPhoneNo.setText(conname);

                textPhoneOnFocus = false;
                textSMSOnFocus = true;
            } else {
                textPhoneOnFocus = true;
                textSMSOnFocus = false;
            }
        } else {
            textPhoneOnFocus = true;
            textSMSOnFocus = false;
        }
    }

    private void AppendStrings(String input) {
        if (textPhoneOnFocus) {
            final String number = input;
            speaker.speak(number);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textPhoneNo.append(number);
                }
            });
        } else {
            final String text = input;
            speaker.speak(text);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textSMS.append(text);
                }
            });
        }

    }

    private void BackSpace() {
        if (textPhoneOnFocus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String old = textPhoneNo.getText().toString();
                    String newStr = "";
                    textPhoneNo.setText("");
                    if (old.length() > 0) {
                        newStr = old.substring(0, old.length() - 1);
                        textPhoneNo.append(newStr);
                    } else {
                        textPhoneNo.setText("");
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String old = textSMS.getText().toString();
                    String newStr = "";
                    textSMS.setText("");
                    if (old.length() > 0) {
                        newStr = old.substring(0, old.length() - 1);
                        textSMS.append(newStr);
                    } else {
                        textSMS.setText("");
                    }
                }
            });
        }

    }

    private void RegisterIntents() {
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    //Everything is fine
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
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
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //register the BroadCastReceivers to listen for a specific broadcast
        //if they "hear" that broadcast, it will activate their onReceive() method
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }

    private void UnRegisterIntents() {
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
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
                ArduinoInputConverter aic = new ArduinoInputConverter();

                if (textPhoneOnFocus) {
                    if (aic.IsSame(input, 192)) {
                        speaker.speak("Opening contact list");
                        SearchRecipient_btn_OnClickEvent();
                    } else if (aic.IsNumber(input)) {
                        AppendStrings(String.valueOf(aic.GetNumber(input)));
                    } else if (aic.IsSame(input, 64)) {
                        BackSpace();
                    } else if (aic.IsSame(input, 128)) {
                        textPhoneOnFocus = false;
                        textSMSOnFocus = true;
                    }
                } else {
                    if (aic.IsForMessaging(input)) {
                        AppendStrings(aic.getChar(input));
                    } else if (aic.IsSame(input, 64)) {
                        BackSpace();
                    } else if (aic.IsSame(input, 128)) {
                        textPhoneOnFocus = false;
                        textSMSOnFocus = true;
                    }else if(aic.IsSame(input, 63)){
                        SendMessage();
                    }else if(aic.IsSame(input, 184)){
                        cancelComposeButton();
                    }
                }


                /*if(aic.IsForMessaging(input)){
                    speaker.speak(input);
                    AppendBody(aic.getChar(input));
                }else if (aic.IsSame(input, 64)){
                    Log.e("ReplyMessageActivity", "Calling BackSpace");
                    BackSpaceBody();
                } else if (aic.IsSame(input, 71)){
                    finish();
                }else if (aic.IsSame(input, 184)){
                    ReplySMS();
                }*/

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

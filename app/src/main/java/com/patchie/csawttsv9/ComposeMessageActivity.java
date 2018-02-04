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
import android.telecom.Call;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

    ArduinoInputConverter aic;
    Speaker speaker;

    /*Button buttonSend;
    Button buttonCancel;*/
    EditText textPhoneNo;
    private boolean textPhoneOnFocus = true;
    EditText textSMS;
    private boolean textSMSOnFocus = false;

    String conname = "";
    String connum = null;

    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";


    /*private static final int DEFAULT_SEND_VAL = 63;
    private static final int DEFAULT_CANCEL_VAL = 184;*/

    //private int DEFAULT_CANCEL_VAL = 184;

    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ComposeMessageActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        setTitle(getString(R.string.ComposeActivity));


        speaker = new Speaker(this, getString(R.string.ComposedWelcomeMessage));

        //To disable clickable
        /*EditText compose = (EditText)findViewById(R.id.compose);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);

        contactnumber.setShowSoftInputOnFocus(false);
        compose.setShowSoftInputOnFocus(false);*/


        textPhoneNo = (EditText) findViewById(R.id.contactnumber);
        textPhoneNo.setShowSoftInputOnFocus(true);
        textPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("ComposeMessage", "ReceiverEditText beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("ComposeMessage", "ReceiverEditText onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("ComposeMessage", "ReceiverEditText afterTextChanged");
                speaker.speakAdd(textPhoneNo.getText().toString());
            }
        });

        textSMS = (EditText) findViewById(R.id.compose);
        textSMS.setShowSoftInputOnFocus(true);
        textSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("ComposeMessage", "BodyEditText beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("ComposeMessage", "BodyEditText onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("ComposeMessage", "BodyEditText afterChanged");
                speaker.speak(textSMS.getText().toString());
            }
        });

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    }

    @Override
    protected void onStart() {
        Log.e("ComposeMessageActivity", "onStart");
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
    }

    @Override
    protected void onPause() {
        Log.e("ComposeMessageActivity", "onPause");
        super.onPause();


        if (speaker.isSpeaking()) {
            Log.e("MainActivity: onPause", "Stopping speaker");
            speaker.stop();
        }

        UnRegisterSmsIntents();

        //Arduino
        StopScanner();
        UnRegisterArduinoIntent();

    }

    @Override
    protected void onResume() {
        Log.e("ComposeMessageActivity", "onResume");
        super.onResume();

        aic = new ArduinoInputConverter(getApplicationContext());
        if (speaker == null) {
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }

        RegisterSmsIntents();

        //Arduino
        RegisterArduinoIntent();
        StartScanner();

        //checking which object to focus
        /*if (textPhoneOnFocus) {
            textPhoneNo.setFocusable(true);
            textSMS.setFocusable(false);
        } else {
            textPhoneNo.setFocusable(false);
            textSMS.setFocusable(true);
        }*/
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

        aic = null;
        //UnRegisterSmsIntents();
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
                    String textToDelete = "";
                    String old = textPhoneNo.getText().toString();
                    String newStr = "";
                    textPhoneNo.setText("");
                    if (old.length() > 0) {
                        textToDelete = old.substring(old.length() - 1);
                        speaker.speak(textToDelete);
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
                    String textToDelete = "";
                    String old = textSMS.getText().toString();
                    String newStr = "";
                    textSMS.setText("");
                    if (old.length() > 0) {
                        textToDelete = old.substring(old.length() - 1);
                        speaker.speak(textToDelete);
                        newStr = old.substring(0, old.length() - 1);
                        textSMS.append(newStr);
                    } else {
                        textSMS.setText("");
                    }
                }
            });
        }

    }

    private void RegisterSmsIntents() {
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    //Everything is fine
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        speaker.speak("Message sent");
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        speaker.speak("Sending Fail");
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        speaker.speak("No Network service, cant send text message");
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        speaker.speak("System error");
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

    private void UnRegisterSmsIntents() {
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    /*This will change focus if the certain combination is received*/
    private void ChangeFocus() {
        if (textPhoneOnFocus == true) {
            textPhoneOnFocus = false;
            textSMSOnFocus = true;
            speaker.speak("Sms body selected");
        } else if (textSMSOnFocus == true) {
            textPhoneOnFocus = true;
            textSMSOnFocus = false;
            speaker.speak("Sms receiver selected");
        }
    }

    private void DetermineControl(int input) {
        if (input == aic.CONTROL_OK()) {
            SendMessage();
        }
        if (input == aic.CONTROL_CANCEL()) {
            cancelComposeButton();
        }
    }

    /*
    * ARDUINO GLOBAL VARIABLE
    * */
    public final String ACTION_USB_PERMISSION = "com.patchie.csawttsv9.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    private void CallBack(int x) {
        ArduinoInputConverter aic = new ArduinoInputConverter(this);

        if (textPhoneOnFocus) {
            if (x == aic.CONTROL_SEARCH()) {
                speaker.speak("Opening contact list");
                SearchRecipient_btn_OnClickEvent();
            }
            if (aic.IsNumber(String.valueOf(x))) {
                AppendStrings(String.valueOf(aic.GetNumber(String.valueOf(x))));
            }
            if (x == aic.CONTROL_BACKSPACE()) {
                BackSpace();
            }
            if (x == aic.CONTROL_FOCUS_CHANGER()) {
                ChangeFocus();
            }
                    /*<Send Cancel>*/
            if (x == aic.CONTROL_OK() || x == aic.CONTROL_CANCEL()) {
                DetermineControl(aic.getDecimal(String.valueOf(x)));
            }
                    /*</Send Cancel>*/
        } else {
            if (aic.IsForMessaging(String.valueOf(x))) {
                AppendStrings(aic.getChar(String.valueOf(x)).toLowerCase());
            }
            if (x == aic.CONTROL_BACKSPACE()) {
                BackSpace();
            }
            if (aic.getDecimal(String.valueOf(x)) == aic.CONTROL_FOCUS_CHANGER()) {
                ChangeFocus();
            }
                    /*<Send Cancel>*/
            if (x == aic.CONTROL_OK() || x == aic.CONTROL_CANCEL()) {
                DetermineControl(aic.getDecimal(String.valueOf(x)));
            }
                    /*</Send Cancel>*/
        }

    }

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
                CallBack(x);

                /*if (textPhoneOnFocus) {
                    if (x == aic.CONTROL_SEARCH()) {
                        speaker.speak("Opening contact list");
                        SearchRecipient_btn_OnClickEvent();
                    }
                    if (aic.IsNumber(input)) {
                        AppendStrings(String.valueOf(aic.GetNumber(input)));
                    }
                    if (x == aic.CONTROL_BACKSPACE()) {
                        BackSpace();
                    }
                    if (x == aic.CONTROL_FOCUS_CHANGER()) {
                        ChangeFocus();
                    }
                    *//*<Send Cancel>*//*
                    if (x == aic.CONTROL_OK() || x == aic.CONTROL_CANCEL()) {
                        DetermineControl(aic.getDecimal(input));
                    }
                    *//*</Send Cancel>*//*
                } else {
                    if (aic.IsForMessaging(input)) {
                        AppendStrings(aic.getChar(input).toLowerCase());
                    }
                    if (x == aic.CONTROL_BACKSPACE()) {
                        BackSpace();
                    }
                    if (aic.getDecimal(input) == aic.CONTROL_FOCUS_CHANGER()) {
                        ChangeFocus();
                    }
                    *//*<Send Cancel>*//*
                    if (x == aic.CONTROL_OK() || x == aic.CONTROL_CANCEL()) {
                        DetermineControl(aic.getDecimal(input));
                    }
                    *//*</Send Cancel>*//*
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

    /*
        Emergency Triggers(Volume Buttons)
        SMS (down + up  + down)
        call (down + up + up + down)
        * */
    boolean isVolDownAllowed = true;
    boolean isVolUpAllowed = false;
    int UpCounter = -1;
    final static int EmergencySms = 0;
    final static int EmergencyCall = 1;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && isVolDownAllowed == true) {
            Log.e("Czar", "standby");
            isVolDownAllowed = false;
            isVolUpAllowed = true;
            return true;
        }
        //for increment
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) && isVolUpAllowed == true) {
            UpCounter++;
            Log.e("Czar", "increment: " + UpCounter);
            //checking if counter exceeds 1 then it must reset
            if (UpCounter > 1) {
                isVolDownAllowed = true;
                isVolUpAllowed = false;
                UpCounter = -1;
                Log.e("Czar", "reset");
                return true;
            }
            return true;
        }
        //checking for emergency type
        String EMERGENCY_TYPE = "Emergency_Type";
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && isVolUpAllowed == true) {
            if (UpCounter == EmergencySms) {
                isVolDownAllowed = true;
                isVolUpAllowed = false;
                UpCounter = -1;
                Log.e("Czar", "SMS");
                Intent intent = new Intent(this, EmergencyActivity.class);
                intent.putExtra(EMERGENCY_TYPE, 0);
                startActivity(intent);
                return true;
            }
            if (UpCounter == EmergencyCall) {
                isVolDownAllowed = true;
                isVolUpAllowed = false;
                UpCounter = -1;
                Log.e("Czar", "CALL");
                Intent intent = new Intent(this, EmergencyActivity.class);
                intent.putExtra(EMERGENCY_TYPE, 1);
                startActivity(intent);
                return true;
            }
        }
        return false;
    }
}

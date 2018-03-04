package com.patchie.csawttsv9;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    SmsManager smsManager = SmsManager.getDefault();
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    PendingIntent sentPI, deliveredPI;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";

    private final static int CREATE_REQUEST_CODE = 0130;
    private static String PREFS_NAME = "CSB";
    private static String EMERGENCY_NUMBER = "Dnumber";
    private String emergency_number = "";
    private static String EMERGENCY_NAME = "Dname";
    private String emergency_name = "";

    public String EMERGENCY_TYPE = "Emergency_Type";
    private boolean IsoutSideCall = true;
    private int Etype = -1;

    EditText ContactNumber;

    Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("EmergencyActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        setTitle(getString(R.string.EmergencyActivity));

        Etype = this.getIntent().getIntExtra(EMERGENCY_TYPE, -1);
        if (Etype == -1) {
            Log.e("EmergencyActivity", "Initializing with welcome");
            speaker = new Speaker(getApplicationContext(), getString(R.string.EmergencyWelcome));
        } else {
            //Speaker
            Log.e("EmergencyActivity", "Initializing without welcome" + Etype);
            //speaker = new Speaker(getApplicationContext());
        }

        //called inside
        IsoutSideCall = false;


        //Assigning edittextbox
        ContactNumber = (EditText) findViewById(R.id.ContactNo_et);
        ContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Etype != -1) {
                    speaker.speak(ContactNumber.getText().toString());
                }else {
                    //speaker.speakAdd(ContactNumber.getText().toString());
                }

            }
        });

    }

    @Override
    protected void onStart() {
        Log.e("EmergencyActivity", "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.e("EmergencyActivity", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.e("EmergencyActivity", "onPause");
        super.onPause();

        if (speaker.isSpeaking()) {
            Log.e("MainActivity: onPause", "Stopping speaker");
            speaker.stop();
        }

        UnRegisterSmsIntent();

        StopScanner();
        UnRegisterArduinoIntent();
    }

    @Override
    protected void onResume() {
        Log.e("EmergencyActivity", "onResume");
        super.onResume();

        if (speaker == null && Etype == -1) {
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }

        RegisterSmsIntent();

        //checking if there is saved number
        if (haveSavedNumber() && Etype == -1) {
            getSavedNumber();
        }

        if (Etype == 0) {
            Etype = -1;
            SendSOS();
            //finish();
        } else if (Etype == 1) {
            Etype = -1;
            EmergencyCall();
            //finish();
        } else {
            //Initialize here welcome message
            //speaker.speakAdd(getString(R.string.EmergencyWelcome));
        }

        RegisterArduinoIntent();
        StartScanner();
    }

    @Override
    protected void onStop() {
        Log.e("EmergencyActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("EmergencyActivity", "onDestroy");
        super.onDestroy();

        speaker.destroy();
    }

    public void SelectContact_OnClickEvent(View view) {
        SearchRecipient_btn_OnClickEvent();
    }

    Intent RecipientIntent;

    private void SearchRecipient_btn_OnClickEvent() {
        if (RecipientIntent == null) {
            RecipientIntent = new Intent(this, SmsRecipientActivity.class);
        }
        startActivityForResult(RecipientIntent, CREATE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                ContactNumber.setText(data.getStringExtra("CONTACT_NUMBER"));
                /*conname = data.getStringExtra("CONTACT_NAME");
                connum = data.getStringExtra("CONTACT_NUMBER");
                textPhoneNo.setEnabled(false);
                textPhoneOnFocus = false;
                textSMSOnFocus = true;
                textPhoneNo.setText(conname);

                textPhoneOnFocus = false;
                textSMSOnFocus = true;*/
            } else {
                /*textPhoneOnFocus = true;
                textSMSOnFocus = false;*/
            }
        } else {
            /*textPhoneOnFocus = true;
            textSMSOnFocus = false;*/
        }
    }

    public void EmergencyCancel_OnClickEvent(View view) {
        finish();
    }

    public void EmergencySave_OnClickEvent(View view) {
        SaveEmergencyContact();
    }

    private void SaveEmergencyContact() {
        SharedPreferences EmergencyContact = getSharedPreferences(PREFS_NAME, this.MODE_PRIVATE);

        //SAVING
        SharedPreferences.Editor edit = EmergencyContact.edit();
        edit.putString("Dnumber", ContactNumber.getText().toString());
        edit.apply();
        edit.commit();
        speaker.speak("Contact Saved!");
        Log.e("EmergencyActivity", "Saved");
        //ContactNumber.setText("");
    }

    public void EmergencySmsBtn_OnClickEvent(View view) {
        SendSOS();
    }

    private void SendSOS() {

        UnRegisterSmsIntent();
        RegisterSmsIntent();
        //checking if there is saved number
        if (haveSavedNumber()) {
            getSavedNumber();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                getPermissionToReadSMS();
            } else {
                try {
                    if (speaker == null) {
                        speaker = new Speaker(getApplicationContext(), "Sending to " + emergency_number);
                        //smsManager.sendTextMessage(emergency_number, null, getString(R.string.EmergencyMessage) + "\n" + "SOS", sentPI, deliveredPI);
                        smsManager.sendTextMessage(emergency_number, null, getString(R.string.EmergencyMessage), sentPI, deliveredPI);
                    } else {
                        speaker.speak("Sending to " + emergency_number);
                        //smsManager.sendTextMessage(emergency_number, null, getString(R.string.EmergencyMessage) + "\n" + "SOS", sentPI, deliveredPI);
                        smsManager.sendTextMessage(emergency_number, null, getString(R.string.EmergencyMessage), sentPI, deliveredPI);
                        //Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            speaker.speak("There is no Saved emergency contact nu,ber, please saved first emergency contact number");
            return;
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

    private boolean haveSavedNumber() {
        boolean ans = false;
        try {
            SharedPreferences EmergencyContact = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String pulledString = EmergencyContact.getString(EMERGENCY_NUMBER, "");
            if (pulledString.length() > 0) {
                ans = true;
            }
        } catch (Exception e) {
            ans = false;
        }
        return ans;
    }

    private void getSavedNumber() {
        SharedPreferences EmergencyContact = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String pulledString = EmergencyContact.getString(EMERGENCY_NUMBER, "");
        emergency_number = pulledString;
        if (!IsoutSideCall && Etype == -1) {
            ContactNumber.setText(pulledString);
        }

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
                        //finish();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.e("ReplySmsActivity", "Generic failure");
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_ERROR_GENERICFAILURE));
                        //finish();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.e("ReplySmsActivity", "No Service");
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_ERROR_NO_SERVICE));
                        //finish();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.e("ReplySmsActivity", "Null PDU");
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_NULL_PDU));
                        //finish();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.e("ReplySmsActivity", "Radio OFF");
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
                        speaker.speak(getString(R.string.SentOnReceived_RESULT_RADIO_OFF));
                        //finish();
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
                        //finish();
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.e("ReplySmsActivity", "Sms Not Delivered");
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        //speaker.speak(getString(R.string.DeliverOnReceived_ResultCancel));
                        //finish();
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
        try {
            unregisterReceiver(smsSentReceiver);
            unregisterReceiver(smsDeliveredReceiver);
        } catch (Exception e) {
            Log.e("EmergencyActivity", "UnRegisterSmsIntent: nothing to do");
        }

    }

    public void EmergencyCallBtn_OnClickEvent(View view) {
        EmergencyCall();
    }

    private void EmergencyCall() {
        if (haveSavedNumber()) {
            getSavedNumber();

            //speaker.speak("Calling " + emergency_number);

            if (speaker == null) {
                speaker = new Speaker(getApplicationContext(), "Calling " + emergency_number);
            } else {
                speaker.speak("Calling " + emergency_number);
                //Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
            }

            //t1.speak("Call", TextToSpeech.QUEUE_FLUSH, null);
            Intent callIntent;
            callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + emergency_number));
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
            //String toSpeak = editNum.getText().toString();
            //t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
        } else {
            speaker.speak("There is no Saved emergency contact nu,ber, please saved first emergency contact number");
            return;
        }

    }

    private void Append(String num) {
        final String x = num;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Czar", x);
                speaker.speak(x);
                ContactNumber.append(x);
                speaker.speakAdd(ContactNumber.getText().toString());
                //PhoneNumber = editNum.getText().toString();
            }
        });
    }

    private void backspace() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ContactNumber.getText().toString().length() >= 1) {
                    String old = ContactNumber.getText().toString().substring(ContactNumber.getText().length() - 1);
                    speaker.speak("Deleting " + old);
                    String newScreen = ContactNumber.getText().toString().substring(0, ContactNumber.getText().toString().length() - 1);
                    //editNum.setText(newScreen);
                    ContactNumber.setText("");
                    ContactNumber.append(newScreen);

                    speaker.speakAdd(ContactNumber.getText().toString());
                }
            }
        });
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

        //appending]
        if (aic.IsNumber(String.valueOf(x))) {
            Append(String.valueOf(aic.GetNumber(String.valueOf(x))));
        }
        //saving emergency contact number
        if(x == aic.CONTROL_OK()){
            SaveEmergencyContact();
        }
        if (x == aic.CONTROL_BACKSPACE()) {
            backspace();
        }
        if (x == aic.CONTROL_SEARCH()){
            SearchRecipient_btn_OnClickEvent();
        }

        if (x == aic.CONTROL_CANCEL()) {
            finish();
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
                ArduinoInputConverter aic = new ArduinoInputConverter(getApplicationContext());

                int x = aic.getDecimal(input);
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

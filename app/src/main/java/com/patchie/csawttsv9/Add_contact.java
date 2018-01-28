package com.patchie.csawttsv9;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Add_contact extends AppCompatActivity {

    Speaker speaker;
    EditText etName;
    EditText etPhone;
    boolean IsNameOnFocus = true;
    boolean IsNumberOnFocus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("AddContactActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setTitle(getString(R.string.AddContactActivity));

        speaker = new Speaker(getApplicationContext(), "Welcome to ADD CONTACT MODULE, In this module you can add a new contact");

        etName = (EditText) findViewById(R.id.et_name);
        etName.setShowSoftInputOnFocus(false);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                speaker.speakAdd(etName.getText().toString());

            }
        });
        etPhone = (EditText) findViewById(R.id.et_mobile_phone);
        etPhone.setShowSoftInputOnFocus(false);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                speaker.speakAdd(etPhone.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        Log.e("AddContactActivity", "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.e("AddContactActivity", "onPause");
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
        Log.e("AddContactActivity", "onResume");
        super.onResume();

        RegisterArduinoIntent();
        StartScanner();

        if (speaker == null) {
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }
    }

    @Override
    protected void onStop() {
        Log.e("AddContactActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("AddContactActivity", "onDestroy");
        super.onDestroy();

        speaker.destroy();
    }

    public void onClick_back_btn(View view) {
        CANCELV2();
    }

    private void CANCELV2() {
        speaker.speak("Canceled");
        finish();
    }

    private void ADD_CONTACT() {
        addContact();
    }

    private void addContact() {

        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_mobile_phone);


        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        int rawContactID = ops.size();

        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, etName.getText().toString())
                .build());
        String toSpeak = etName.getText().toString();
        //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, etPhone.getText().toString())
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build());
        String toSpeak1 = etName.getText().toString();
        //t1.speak(toSpeak1, TextToSpeech.QUEUE_ADD, null);

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            speaker.speak("Added new contact successfully");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    public void OnClick_add(View view) {
        ADD_CONTACT();
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
        /**
         * Codes here
         * Don't forget to null aic
         * */
        Log.e("AddContact", "x: " + x + " : " + aic.CONTROL_FOCUS_CHANGER());
        if (IsNameOnFocus) {
            if (x == aic.CONTROL_FOCUS_CHANGER()) {
                ChangeFocus();
            }
            if (x == aic.CONTROL_BACKSPACE()) {
                BackSpace();
            }
            if (aic.IsForMessaging(String.valueOf(x))) {
                AppendString(aic.getChar(String.valueOf(x)));
            }
            if (x == aic.CONTROL_OK()) {
                addContact();
            }
        } else if (IsNumberOnFocus) {
            if (x == aic.CONTROL_FOCUS_CHANGER()) {
                ChangeFocus();
            }
            if (x == aic.CONTROL_BACKSPACE()) {
                BackSpace();
            }
            if (aic.IsNumber(String.valueOf(x))) {
                AppendString(String.valueOf(aic.GetNumber(String.valueOf(x))));
            }
            if (x == aic.CONTROL_OK()) {
                addContact();
            }
        }
    }

    private void ChangeFocus() {
        Log.e("AddContact", "ChangeFocus");
        if (IsNameOnFocus == true) {
            IsNameOnFocus = false;
            IsNumberOnFocus = true;
            speaker.speak("Please enter number");
        } else {
            IsNameOnFocus = true;
            IsNumberOnFocus = false;
            speaker.speak("Please enter name");
        }
    }

    private void AppendString(String input) {
        if (IsNameOnFocus) {
            final String conName = input;
            speaker.speak(input);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etName.append(conName);
                }
            });
        }
        if (IsNumberOnFocus) {
            final String conNum = input;
            speaker.speak(input);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etPhone.append(conNum);
                }
            });
        }
    }

    private void BackSpace() {
        if (IsNameOnFocus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String textToDelete = "";
                    String old = etName.getText().toString();
                    String newStr = "";
                    etName.setText("");
                    if (old.length() > 0) {
                        textToDelete = old.substring(old.length() - 1);
                        speaker.speak("Deleting " + textToDelete);
                        newStr = old.substring(0, old.length() - 1);
                        etName.append(newStr);
                    } else {
                        etName.setText("");
                    }
                }
            });
        }
        if (IsNumberOnFocus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String textToDelete = "";
                    String old = etPhone.getText().toString();
                    String newStr = "";
                    etPhone.setText("");
                    if (old.length() > 0) {
                        textToDelete = old.substring(old.length() - 1);
                        speaker.speak("Deleting" + textToDelete);
                        newStr = old.substring(0, old.length() - 1);
                        etPhone.append(newStr);
                    } else {
                        etPhone.setText("");
                    }
                }
            });
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

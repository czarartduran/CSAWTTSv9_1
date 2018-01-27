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
    TextToSpeech t1;
    EditText etName;
    EditText etPhone;

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        Log.e("Add_contact", "onPause");

        StopScanner();
        unregisterReceiver(broadcastReceiver);
        super.onPause();
        speaker.destroy();
    }

    @Override
    protected void onResume() {
        Log.e("Add_contact", "onResume");
        super.onResume();
        RegisterIntent();
        StartScanner();
        speaker = new Speaker(getApplicationContext());
        speaker.speakAdd("Please Input number");
    }

    @Override
    protected void onStop() {
        Log.e("Add_contact", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("Add_contact", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        setTitle(getString(R.string.AddContactActivity));




        speaker = new Speaker(getApplicationContext(), "Welcome to ADD CONTACT MODULE, In this module you can add a new contact");



        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }

            }
        });
        etName = (EditText) findViewById(R.id.et_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                speaker.speak(etName.getText().toString());

            }
        });
        etPhone = (EditText) findViewById(R.id.et_mobile_phone);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                speaker.speak(etPhone.getText().toString());
            }
        });
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
        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, etPhone.getText().toString())
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build());
        String toSpeak1 = etName.getText().toString();
        t1.speak(toSpeak1, TextToSpeech.QUEUE_ADD, null);

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
    private void RegisterIntent() {
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }
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
                data = new String(bytes, "UTF-8");
                final String input = data;
                ArduinoInputConverter aic = new ArduinoInputConverter(getApplicationContext());

                int c = aic.getDecimal(input);
                if (c == aic.CONTROL_ADD_ADDCONTACT()) {
                    addContact();
                }
                if (c == aic.CONTROL_CANCELV2()) {
                    CANCELV2();
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
                else {
                    Log.e("jibeh SERIAL", "PERMISSION NOT GRANTED");
                }


            }

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


}

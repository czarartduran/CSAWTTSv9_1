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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DialerActivity extends AppCompatActivity {
    private String PhoneNumber = "";
    EditText editNum;
    Speaker speaker;
    //TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("DialerActivity", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        setTitle(getString(R.string.DialerActivity));

        speaker = new Speaker(getApplicationContext(), getString(R.string.DialerWelcomeMessage));

        //assigning number_tb
        editNum = (EditText) findViewById(R.id.number_tb);
        editNum.setShowSoftInputOnFocus(false);
        editNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                speaker.speakAdd(editNum.getText().toString());
            }
        });

    }

    @Override
    protected void onStart() {
        Log.e("DialerActivity", "");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.e("DialerActivity", "");
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
        Log.e("DialerActivity", "");
        super.onResume();

        RegisterArduinoIntent();
        StartScanner();

        if (speaker == null) {
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("DialerActivity", "");
        super.onDestroy();

        speaker.destroy();
    }


    public void numPad_OnClickEvent(View view) {
        NumPad(view);
    }

    protected void NumPad(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                AppendNumber("1");
                //t1.speak("one, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak = editNum.getText().toString();
                //t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn2:
                AppendNumber("2");
                //t1.speak("two, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak2 = editNum.getText().toString();
                //t1.speak(toSpeak2, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn3:
                AppendNumber("3");
                //t1.speak("three, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak3 = editNum.getText().toString();
                //t1.speak(toSpeak3, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn4:
                AppendNumber("4");
                //t1.speak("four, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak4 = editNum.getText().toString();
                //t1.speak(toSpeak4, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn5:
                AppendNumber("5");
                //t1.speak("five, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak5 = editNum.getText().toString();
                //t1.speak(toSpeak5, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn6:
                AppendNumber("6");
                //t1.speak("six, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak6 = editNum.getText().toString();
                //t1.speak(toSpeak6, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn7:
                AppendNumber("7");
                //t1.speak("seven, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak7 = editNum.getText().toString();
                //t1.speak(toSpeak7, TextToSpeech.QUEUE_ADD, null);

                break;
            case R.id.btn8:
                AppendNumber("8");
                //t1.speak("eight, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak8 = editNum.getText().toString();
                //t1.speak(toSpeak8, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn9:
                AppendNumber("9");
                //t1.speak("nine, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak9 = editNum.getText().toString();
                //t1.speak(toSpeak9, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn10:
                AppendNumber("0");
                //t1.speak("zero, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak0 = editNum.getText().toString();
                //t1.speak(toSpeak0, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn11:
                AppendNumber("*");
                //t1.speak("Asterisk, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeakast = editNum.getText().toString();
                //t1.speak(toSpeakast, TextToSpeech.QUEUE_ADD, null);
                break;
            case R.id.btn12:
                AppendNumber("#");
                //t1.speak("Hash, ", TextToSpeech.QUEUE_FLUSH, null);
                String toSpeakhash = editNum.getText().toString();
                //t1.speak(toSpeakhash, TextToSpeech.QUEUE_ADD, null);
                break;
            default:
                break;
        }
    }

    private void AppendNumber(String num) {
        Log.e("Czar", num);
        speaker.speak(num);
        editNum.append(num);
        PhoneNumber = editNum.getText().toString();
    }

    private void Append(String num){
        final String x = num;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Czar", x);
                speaker.speak(x);
                editNum.append(x);
                PhoneNumber = editNum.getText().toString();
            }
        });
    }

    public void backspace_OnClickEvent(View view) {

        BackSpace();
    }

    private void BackSpace() {
        if (editNum.getText().toString().length() >= 1) {
            String old = editNum.getText().toString().substring(editNum.getText().length() - 1);
            speaker.speak("Deleting " + old);
            String newScreen = editNum.getText().toString().substring(0, editNum.getText().toString().length() - 1);
            //editNum.setText(newScreen);
            editNum.setText("");
            editNum.append(newScreen);
        }
    }

    private void backspace(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (editNum.getText().toString().length() >= 1) {
                    String old = editNum.getText().toString().substring(editNum.getText().length() - 1);
                    speaker.speak("Deleting " + old);
                    String newScreen = editNum.getText().toString().substring(0, editNum.getText().toString().length() - 1);
                    //editNum.setText(newScreen);
                    editNum.setText("");
                    editNum.append(newScreen);
                }
            }
        });
    }

    public void cancelbtn_OnClickEvent(View view) {
        CancelBtn();
    }

    private void CancelBtn() {
        //speaker.speak("Canceled");
        finish();
    }

    public void dialbtn_OnClickEvent(View view) {
        Call();
    }

    private void Call() {
        speaker.speak("Calling " + PhoneNumber);
        //t1.speak("Call", TextToSpeech.QUEUE_FLUSH, null);
        Intent callIntent;
        callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PhoneNumber));
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
        String toSpeak = editNum.getText().toString();
        //t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

    }

    public void repeatbtn_OnClickEvent(View v) {
        repeat();
    }

    private void repeat() {
        String toSpeak = PhoneNumber;
        //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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
        if (aic.IsNumber(String.valueOf(x))) {
            Append(String.valueOf(aic.GetNumber(String.valueOf(x))));
        }
        if (x == aic.CONTROL_BACKSPACE()) {
            backspace();
        }
        if (x == aic.CONTROL_OK()) {
            Call();
        }
        if (x == aic.CONTROL_CANCEL()) {
            CancelBtn();
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

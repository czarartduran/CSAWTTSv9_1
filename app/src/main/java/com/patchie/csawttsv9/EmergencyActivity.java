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

    EditText ContactNumber;

    Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("EmergencyActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        setTitle(getString(R.string.EmergencyActivity));

        //Speaker
        speaker = new Speaker(getApplicationContext());

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
                speaker.speak(ContactNumber.getText().toString());
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
    }

    @Override
    protected void onResume() {
        Log.e("EmergencyActivity", "onResume");
        super.onResume();

        if (speaker == null) {
            Log.e("MainActivity: onResume", "Initializing Speaker");
            speaker = new Speaker(getApplicationContext());
        }

        RegisterSmsIntent();

        //checking if there is saved number
        if (haveSavedNumber()) {
            getSavedNumber();
        }
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
        ContactNumber.setText("");
    }

    public void EmergencySmsBtn_OnClickEvent(View view) {
        SendSOS();
    }

    private void SendSOS() {
        //checking if there is saved number
        if (haveSavedNumber()) {
            getSavedNumber();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                getPermissionToReadSMS();
            } else {
                try {
                    speaker.speak("Sending to " + emergency_number);
                    smsManager.sendTextMessage(emergency_number, null, getString(R.string.EmergencyMessage) + "\n" + "SOS", sentPI, deliveredPI);
                    //Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
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
        ContactNumber.setText(pulledString);
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
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    public void EmergencyCallBtn_OnClickEvent(View view) {
        EmergencyCall();
    }

    private void EmergencyCall() {
        if (haveSavedNumber()) {
            getSavedNumber();

            speaker.speak("Calling " + emergency_number);
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
}

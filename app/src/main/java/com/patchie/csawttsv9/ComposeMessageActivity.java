package com.patchie.csawttsv9;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeMessageActivity extends AppCompatActivity {
    SmsManager smsManager = SmsManager.getDefault();
    private final static int CREATE_REQUEST_CODE = 0130;

    Speaker speaker;

    Button buttonSend;
    Button buttonCancel;
    EditText textPhoneNo;
    EditText textSMS;

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

        speaker = new Speaker(getApplicationContext(), "Welcome");

        //To disable clickable
        /*EditText compose = (EditText)findViewById(R.id.compose);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);

        contactnumber.setShowSoftInputOnFocus(false);
        compose.setShowSoftInputOnFocus(false);*/


        textPhoneNo = (EditText) findViewById(R.id.contactnumber);
        textSMS = (EditText) findViewById(R.id.compose);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

    }

    @Override
    protected void onStart() {
        Log.e("ComposeMessageActivity", "onStart");
        super.onStart();

        RegisterIntents();
    }

    @Override
    protected void onPause() {
        Log.e("ComposeMessageActivity", "onPause");
        super.onPause();

        speaker.destroy();
    }

    @Override
    protected void onResume() {
        Log.e("ComposeMessageActivity", "onResume");
        super.onResume();

        speaker = new Speaker(getApplicationContext());
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

        UnRegisterIntents();
        finish();
    }

    private void cancelComposeButton(){
        finish();
    }

    public void CancelBtn_OnClick_Event(View view) {
        cancelComposeButton();
    }

    private void SendMessage(){
        String finalrec = "";
        String phoneNo = textPhoneNo.getText().toString();
        String sms = textSMS.getText().toString();

        if (textPhoneNo.isEnabled() == true){
            finalrec = textPhoneNo.getText().toString();
        }else {
            finalrec = connum;
        }

        /*if (connum == null){
            finalrec = phoneNo;
        }else {
            finalrec = connum;
        }*/

        try {
            smsManager.sendTextMessage(finalrec, null,  sms, sentPI, deliveredPI);
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
    private void SearchRecipient_btn_OnClickEvent(){
        if (RecipientIntent == null){
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
                textPhoneNo.setText(conname);
            }
        }
    }

    private void RegisterIntents(){
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
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
                switch(getResultCode())
                {
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

    private void UnRegisterIntents(){
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }
}

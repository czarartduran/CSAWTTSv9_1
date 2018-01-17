package com.patchie.csawttsv9;

import android.content.Context;
import android.content.Intent;
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

    Button buttonSend;
    Button buttonCancel;
    EditText textPhoneNo;
    EditText textSMS;

    String conname = "";
    String connum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        setTitle(getString(R.string.ComposeActivity));


        //To disable clickable
        /*EditText compose = (EditText)findViewById(R.id.compose);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);

        contactnumber.setShowSoftInputOnFocus(false);
        compose.setShowSoftInputOnFocus(false);*/


        textPhoneNo = (EditText) findViewById(R.id.contactnumber);
        textSMS = (EditText) findViewById(R.id.compose);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);



    }

    @Override
    protected void onDestroy() {
        Log.e("Czar","Compose called its destroy");
        super.onDestroy();
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
            smsManager.sendTextMessage(finalrec, null,  sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_SHORT).show();
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
}

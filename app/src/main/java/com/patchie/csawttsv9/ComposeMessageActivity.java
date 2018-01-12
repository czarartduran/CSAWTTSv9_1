package com.patchie.csawttsv9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeMessageActivity extends AppCompatActivity {

    Button buttonSend;
    Button buttonCancel;
    EditText textPhoneNo;
    EditText textSMS;

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

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.contactnumber);
        textSMS = (EditText) findViewById(R.id.compose);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String phoneNo = textPhoneNo.getText().toString();
                String sms = textSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();} catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

        });

    }

    private void cancelComposeButton()
    {
        startActivity(new Intent(ComposeMessageActivity.this, SMSActivity.class));
    }
    public void CancelBtn_OnClick_Event(View view) {

        cancelComposeButton();

    }
}

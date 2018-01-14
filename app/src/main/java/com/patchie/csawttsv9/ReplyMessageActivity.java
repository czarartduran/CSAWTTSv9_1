package com.patchie.csawttsv9;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReplyMessageActivity extends AppCompatActivity {
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    SmsManager smsManager = SmsManager.getDefault();

    private EditText editText;
    private TextView receiver_tv;
    private String _contactName;
    private String _contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);
        setTitle(getString(R.string.ReplyActivity));

        Intent intent = getIntent();
        _contactName = intent.getStringExtra("contactName");
        _contactNumber = intent.getStringExtra("contactNumber");

        receiver_tv = (TextView) findViewById(R.id.receiver_tv);
        receiver_tv.setText(_contactName);

        editText = (EditText) findViewById(R.id.editText);
        editText.setShowSoftInputOnFocus(true);


    }

    public void ReplySMS_Back_btn_OnClick_Event(View view) {
        finish();
    }

    public void ReplySMS_SEND_btn_OnClick_Event(View view) {
        ReplySMS();
    }

    private void ReplySMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            getPermissionToReadSMS();
        } else{
            try{
                smsManager.sendTextMessage(_contactNumber, null, editText.getText().toString(), null, null);
                Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

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
}

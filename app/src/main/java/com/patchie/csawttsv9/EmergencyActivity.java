package com.patchie.csawttsv9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EmergencyActivity extends AppCompatActivity {

    private final static int CREATE_REQUEST_CODE = 0130;
    EditText ContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("EmergencyActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        setTitle(getString(R.string.EmergencyActivity));

        //Assigning edittextbox
        ContactNumber= (EditText)findViewById(R.id.ContactNo_et);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}

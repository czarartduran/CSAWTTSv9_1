//5:47pm 1/12/18 jibeh
package com.patchie.csawttsv9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class CallActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        setTitle(getString(R.string.CallActivity));


    }

    public void Dial_btn_OnClick_Event(View view) {
        dial_btn_OnClick_Event();
    }

    private void dial_btn_OnClick_Event(){
        callDialActivity();
    }

    Intent dialActivityIntent;
    private void callDialActivity(){
        if (dialActivityIntent == null){
            dialActivityIntent = new Intent(CallActivity.this, Dial_activity.class);
        }
        startActivity(dialActivityIntent);
    }

    public void SelectCon_btn_OnClick_Event(View view) {
        selectCon_btn_OnClick_Event();
    }

    Intent contactListIntent;
    private void selectCon_btn_OnClick_Event(){
        if (contactListIntent == null){
            contactListIntent = new Intent(CallActivity.this, View_contacts.class);
        }
        startActivity(contactListIntent);
    }

    public void Back_btn_OnClickEvent(View view) {
        finish();
    }
}
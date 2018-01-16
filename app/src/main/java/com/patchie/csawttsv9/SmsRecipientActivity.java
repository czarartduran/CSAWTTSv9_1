package com.patchie.csawttsv9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsRecipientActivity extends AppCompatActivity {

    CSB csb;

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_recipient);
        setTitle(getString(R.string.SmsRecipientActivity));

        ListView contact_lv = findViewById(R.id.contact_lv);

        csb = new CSB(this);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, csb.CONTACTLIST());
        contact_lv.setAdapter(arrayAdapter);
    }


}

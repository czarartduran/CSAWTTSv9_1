package com.patchie.csawttsv9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ComposeMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        setTitle(getString(R.string.ComposeActivity));


        EditText compose = (EditText)findViewById(R.id.compose);
        EditText contactnumber = (EditText)findViewById(R.id.contactnumber);

        contactnumber.setShowSoftInputOnFocus(false);
        compose.setShowSoftInputOnFocus(false);

    }
}

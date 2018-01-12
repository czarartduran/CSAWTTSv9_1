package com.patchie.csawttsv9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ReplyMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);
        setTitle(getString(R.string.ReplyActivity));

        EditText editText = (EditText)findViewById(R.id.editText);

        editText.setShowSoftInputOnFocus(false);
    }
}

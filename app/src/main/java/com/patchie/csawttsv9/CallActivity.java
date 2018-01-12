//5:47pm 1/12/18 jibeh
package com.patchie.csawttsv9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        Button buttdial = findViewById(R.id.dialbtn);
        Button buttsc = findViewById(R.id.scbtn);
        Button backbtn = findViewById(R.id.backbtn);
        buttdial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (CallActivity.this,Dial_activity.class);
                startActivity(myIntent);

            }
        });

        buttsc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (CallActivity.this,Contact_list.class);
                startActivity(myIntent);


            }
        });
        backbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
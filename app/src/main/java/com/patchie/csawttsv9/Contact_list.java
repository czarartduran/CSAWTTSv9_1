package com.patchie.csawttsv9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class Contact_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Button buttviewC = (Button) findViewById(R.id.viewC);
        Button buttaddC = (Button) findViewById(R.id.addC);
        Button backbtn = (Button)findViewById(R.id.backbtn);
        buttviewC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (Contact_list.this,View_contacts.class);
                startActivity(myIntent);

            }
        });

        buttaddC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent (Contact_list.this,Add_contact.class);
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


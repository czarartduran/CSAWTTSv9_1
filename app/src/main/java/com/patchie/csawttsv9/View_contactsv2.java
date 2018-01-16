package com.patchie.csawttsv9;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class View_contactsv2 extends Activity {
    private ListView contactListview;
    ContactlistAdapter adp;
    private int selectedIndex;
    Button nxtbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contactsv2);
        contactListview = (ListView) findViewById(R.id.myListview);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();

        //custom adapter
        adp = new ContactlistAdapter(View_contactsv2.this, cursor);
        contactListview.setAdapter(adp);

        //para sa next buttom sana haha
        nxtbtn = (Button)findViewById(R.id.nextButton);
        nxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

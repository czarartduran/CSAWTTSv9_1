package com.patchie.csawttsv9;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.ListActivity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.View.OnClickListener;

public class View_contacts extends ListActivity {


    @Override
    public long getSelectedItemId() {
        // TODO Auto-generated method stub
        return super.getSelectedItemId();
    }

    @Override
    public int getSelectedItemPosition() {
        // TODO Auto-generated method stub
        return super.getSelectedItemPosition();
    }

    ListView listView;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        Button backButton = (Button)this.findViewById(R.id.backbtn);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        setListAdapter(listadapter);


        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }



}
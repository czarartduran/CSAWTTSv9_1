package com.patchie.csawttsv9;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.app.ListActivity;
import android.database.Cursor;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class View_contacts extends ListActivity {
    Speaker _speak;

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
        setTitle(R.string.ContactsListing);
        _speak = new Speaker(getApplicationContext());


        //cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        startManagingCursor(cursor);

        /*cursor checker*/
        Log.e("Czar", "Row Count: " + cursor.getCount());
        Log.e("Czar","Column Count: " + cursor.getColumnCount());
        //Uncomment this to see all column included in the sms query
        String[] asx = cursor.getColumnNames();
        String StrAsx = "";
        for (int i=0; i < asx.length; i++){
            //Log.e("Czar","SMS query Index " + i + ": " + asx[i]);
            StrAsx += asx[i] + " | ";
        }
        Log.e("Czar",StrAsx);
        //plotting
        if (cursor.getCount() > 0 && cursor.moveToFirst()){
            do{
                String str = "";
                for (int i=0; i < asx.length; i++){
                    str += "Index " + i + " "+ cursor.getColumnName(i) + ": " + cursor.getString(i) + " | ";
                    Log.e("Czar","query Index " + i + ": " + asx[i] + " = " + cursor.getString(i));
                }
                Log.e("Czar", "Phonebook| " + str);
            }while (cursor.moveToNext());
            //return;
        }

        /*cursor checker*/

        //Contains the Name, number, and id of a specific contact
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};

        //items for simple_list_item_2
        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        setListAdapter(listadapter);

        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    //Add contact event
public void ADDCON (View view){
    Intent myIntent = new Intent (View_contacts.this,Add_contact.class);
    startActivity(myIntent);
}
    //back event
    public void ViewConBack_btn_OnClick_Event(View view) {
        finish();
    }


    private void Speak(String s) {
        _speak.speak(s);
    }





}

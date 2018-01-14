package com.patchie.csawttsv9;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SMSActivity extends AppCompatActivity {

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    public static boolean active = false;
    private static SMSActivity inst;
    private Speaker _speak;

    //private ArrayList<String> smsMessagesList;
    private ArrayList<String> smsMessagesList = new ArrayList<>();
    private ArrayList<String> _smsMessagesList;
    private ListView messages;
    private ArrayAdapter arrayAdapter;
    private EditText input;
    private SmsManager smsManager = SmsManager.getDefault();

    //Czar Art Duran
    //Variable ***Global
    private boolean _haveReadContactsPermission;
    private boolean _haveReadSmsPermission;
    private int selectedIndex = 0;
    private Cursor smsInboxCursor;

    public static SMSActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        setTitle(getString(R.string.SMSActivity));

        //Getting Passed ArrayList
        _smsMessagesList = this.getIntent().getStringArrayListExtra("SMSLIST");
        //transfer to orig
        smsMessagesList = _smsMessagesList;
        //Loading query
        smsInboxCursor = this.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        //Initializing Speaker
        _speak = new Speaker(getApplicationContext());

        this.startService(new Intent(this, QuickResponseService.class));
        messages = findViewById(R.id.messages);
        messages.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //setting the adapter for sms
        //savedInstanceState.putStringArrayList("SMSLIST", smsMessagesList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);

        //Check if it have rights to access contacts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadContacts();
        }

        //Check if it have rights to read sms
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        }

        // sms and contact read permission must be true before attempting to get data
        /*if (HaveReadContactsPermission() == true && HaveReadSmsPermission() == true) {
            refreshSmsInbox();
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e("Czar", "onStart");

        active = true;
        inst = this;

        _speak = new Speaker(this);

        /*if (HaveReadContactsPermission() == true && HaveReadSmsPermission() == true) {
            refreshSmsInbox();
        }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.e("Czar", "onResume");
        super.onResume();
    }

    /*@Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

       *//* outState.putInt("someVarA", someVarA);
        outState.putString("someVarB", someVarB);*//*
        savedInstanceState.putStringArrayList("smsMessagesList", smsMessagesList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        *//*someVarA = savedInstanceState.getInt("someVarA");
        someVarB = savedInstanceState.getString("someVarB");*//*
        smsMessagesList = savedInstanceState.getStringArrayList("smsMessagesList");
    }*/


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        active = false;
        _speak.destroy();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

    private boolean HaveReadContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            _haveReadContactsPermission = false;
            return _haveReadContactsPermission;
        } else {
            _haveReadContactsPermission = true;
            return _haveReadContactsPermission;
        }
    }

    private boolean HaveReadSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            _haveReadSmsPermission = false;
            return _haveReadSmsPermission;
        } else {
            _haveReadSmsPermission = true;
            return _haveReadSmsPermission;
        }
    }

    public void updateInbox(final String smsMessage) {
        Log.e("Czar", " updateInbox had been called");
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
            HaveReadSmsPermission();
        }
    }

    public void getPermissionToReadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
            HaveReadContactsPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (!_haveReadSmsPermission) return;
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (!_haveReadContactsPermission) return;
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void refreshSmsInbox() {
        Toast.makeText(this, "Please wait!", Toast.LENGTH_LONG);
        ContentResolver contentResolver = getContentResolver();
        //Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexBody = smsInboxCursor.getColumnIndex("body");

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) {
            return;
        } else {
            CSB csb = new CSB(this);
            Log.e("Czar","ArrayList Before clearing: " + smsMessagesList.size());
            arrayAdapter.clear();
            String addressIntro = getString(R.string.AddressIntro) + " ";
            String bodyIntro = getString(R.string.BodyIntro) + " ";
            do {
                /*String str = addressIntro + getContactName(this,
                        smsInboxCursor.getString(indexAddress)) + "\n" +
                        bodyIntro + smsInboxCursor.getString(indexBody) + "\n";*/
                String str = addressIntro + csb.getContactName(this,
                        smsInboxCursor.getString(indexAddress)) + "\n" +
                        bodyIntro + smsInboxCursor.getString(indexBody);
                //Log.e("Czar", "Loading: " + str);
                // if (smsInboxCursor.getString(indexAddress).equals("PHONE NUMBER HERE")) {
                arrayAdapter.add(str);
                Log.e("Czar", "smsMessageList: " + smsMessagesList.size());
                //  }
            } while (smsInboxCursor.moveToNext());
        }
    }

    public void ComposeOnClickEvent(View view) {
        CallComposeActivity();

    }

    private void CallComposeActivity() {
        startActivity(new Intent(SMSActivity.this, ComposeMessageActivity.class));
    }

    public void PreviousButtonOnClickEvent(View view) {
        //calling PreviousMessage function
        PreviousMessage();
    }

    private void PreviousMessage() {
        _speak.stop();
        Log.e("Czar", "Last selected Index: " + selectedIndex);
        int listviewcount = this.messages.getAdapter().getCount();
        Log.e("Czar", "Total listviewcount: " + listviewcount);
        if (selectedIndex >= 0 && selectedIndex <= listviewcount - 1) {
            Log.e("Czar", "Selected Index: " + selectedIndex);
            Log.e("Czar", "Selected Message: " + this.messages.getItemAtPosition(selectedIndex));
            //Toast.makeText(getApplicationContext(), this.messages.getItemAtPosition(selectedIndex).toString(), Toast.LENGTH_SHORT).show();
            Speak(this.messages.getItemAtPosition(selectedIndex).toString());
            if (selectedIndex > 0) {
                selectedIndex--;
            } else {
                Log.e("Czar", "Begging of Message List");
                Toast.makeText(getApplicationContext(), "Begging List", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void NextButtonOnClickEvent(View view) {
        //calling NextMessage function
        NextMessage();
    }

    private void NextMessage() {
        Log.e("Czar", "Last selected Index: " + selectedIndex);
        int listviewcount = this.messages.getAdapter().getCount();
        Log.e("Czar", "Total listviewcount: " + listviewcount);
        if (selectedIndex >= 0 && selectedIndex <= listviewcount - 1) {
            Log.e("Czar", "Selected Index: " + selectedIndex);
            Log.e("Czar", "Selected Message: " + this.messages.getItemAtPosition(selectedIndex));
            //Toast.makeText(getApplicationContext(), this.messages.getItemAtPosition(selectedIndex).toString(), Toast.LENGTH_SHORT).show();
            Speak(this.messages.getItemAtPosition(selectedIndex).toString());
            if (selectedIndex < listviewcount - 1) {
                selectedIndex++;
            } else {
                Log.e("Czar", "End of Message List");
                Toast.makeText(getApplicationContext(), "End List", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Speak(String TextToRead) {
        _speak.speak(TextToRead);
    }

    public void ReplyButtonOnClickEvent(View view) {
        replyButtonOnClickEvent();
    }

    private void replyButtonOnClickEvent() {
        Log.e("Czar", "replyButtonOnClickEvent");
        Log.e("Czar","AdapterSize: " + arrayAdapter.getCount());
        Log.e("Czar", "SMSArraylistSize: " + smsMessagesList.size());

        smsInboxCursor.moveToPosition(selectedIndex);
        Log.e("Czar", "SelectedIndex: "+ selectedIndex);
        String Cname = getContactName(this, smsInboxCursor.getString(2));
        String Cnumber = smsInboxCursor.getString(2);

        Log.e("Czar", "Reply: " + Cname);

        Intent intent = new Intent(getApplicationContext(), ReplyMessageActivity.class);
        intent.putExtra("contactName", Cname);
        intent.putExtra("contactNumber", Cnumber);
        startActivity(intent);

        //startActivity(new Intent(SMSActivity.this, ReplyMessageActivity.class));
    }

    /**
     * This return Contact name given its contact number
     */
    public static String getContactName(Context context, String phoneNo) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        //if there is no returned result
        if (cursor == null) {
            return phoneNo;
        }
        String Name = phoneNo;
        if (cursor.moveToFirst()) {
            Name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return Name;
    }
}

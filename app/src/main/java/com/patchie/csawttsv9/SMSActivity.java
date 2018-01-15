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
    private int selectedIndex = -1;
    private Cursor smsInboxCursor;

    public static SMSActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        setTitle(getString(R.string.SMSActivity));

        Log.e("Czar", "onCreate");

        if (smsMessagesList.isEmpty()) {
            //Getting Passed ArrayList
            _smsMessagesList = this.getIntent().getStringArrayListExtra("SMSLIST");
            //transfer to orig
            smsMessagesList = _smsMessagesList;
        }

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


    private boolean ResetSelectedIndex = false;
    @Override
    protected void onResume() {
        Log.e("Czar", "onResume");
        if (ResetSelectedIndex){
            selectedIndex =-1;
        }
        super.onResume();
    }

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
            Log.e("Czar", "ArrayList Before clearing: " + smsMessagesList.size());
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
        ResetSelectedIndex = true;
        startActivity(new Intent(SMSActivity.this, ComposeMessageActivity.class));
    }

    public void PreviousButtonOnClickEvent(View view) {
        //calling PreviousMessage function
        PreviousMessage();
    }

    private void PreviousMessage() {
        _speak.stop();
        int listviewcount = this.messages.getAdapter().getCount();

        if (selectedIndex >= 0 && selectedIndex < listviewcount) {
            if (selectedIndex >= 0 && selectedIndex -1 >= 0) {
                selectedIndex--;
                Speak(this.messages.getItemAtPosition(selectedIndex).toString());
            } else {
                selectedIndex = 0;
                Speak("you are at the beginning of the List");
                Speak(this.messages.getItemAtPosition(0).toString());
                Toast.makeText(getApplicationContext(), "Beginning List", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void NextButtonOnClickEvent(View view) {
        //calling NextMessage function
        NextMessage();
    }

    private boolean LoadMoreSms = false;

    private void NextMessage() {
        if (LoadMoreSms) {
            Toast.makeText(getApplicationContext(), "Loading more SMS", Toast.LENGTH_SHORT).show();
            Speak("Loading more SMS");
            CSB csb = new CSB(this, 5 + smsMessagesList.size());
            smsMessagesList = csb.SMSLIST();
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
            messages.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            csb = null;
            LoadMoreSms = false;
            return;
        }

        int listviewcount = this.messages.getAdapter().getCount();
        if (selectedIndex >= -1 && selectedIndex < listviewcount) {
            if (selectedIndex < listviewcount && selectedIndex +1 < listviewcount) {
                selectedIndex++;
                Speak(this.messages.getItemAtPosition(selectedIndex).toString());
            } else {
                Speak("you are at the end of the list");
                Speak(this.messages.getItemAtPosition(selectedIndex).toString());
                Toast.makeText(getApplicationContext(), "End List", Toast.LENGTH_SHORT).show();
                LoadMoreSms = true;
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
        if (selectedIndex < 0){
            Speak("Please select message to reply");
            return;
        }
        ResetSelectedIndex = false;
        CSB csb = new CSB();

        smsInboxCursor.moveToPosition(selectedIndex);
        int address = smsInboxCursor.getColumnIndex("address");
        String Cname = csb.getContactName(this, smsInboxCursor.getString(address));
        String Cnumber = smsInboxCursor.getString(address);

        Log.e("Czar", "Reply: " + Cname);

        csb = null;
        Intent intent = new Intent(getApplicationContext(), ReplyMessageActivity.class);
        intent.putExtra("contactName", Cname);
        intent.putExtra("contactNumber", Cnumber);
        startActivity(intent);
    }


}

package com.patchie.csawttsv9;

import java.util.ArrayList;

import android.support.v7.app.AppCompatActivity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Add_contact extends AppCompatActivity {
Speaker speaker;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        speaker = new Speaker(getApplicationContext(), "Welcome to ADD CONTACT MODULE, In this module you can add a new contact");
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.ENGLISH);
                }

            }
        });

        OnClickListener addcontact = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = (EditText)findViewById(R.id.et_name);
                EditText etPhone = (EditText)findViewById(R.id.et_mobile_phone);

                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                int rawContactID = ops.size();

                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_TYPE,null)
                        .withValue(RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.DISPLAY_NAME, etName.getText().toString())
                        .build());
                String toSpeak = etName.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, etPhone.getText().toString())
                        .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                        .build());
                String toSpeak1 = etName.getText().toString();
                t1.speak(toSpeak1, TextToSpeech.QUEUE_ADD, null);

                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    speaker.speak("Added new contact successfully");
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }

            }
        };

        Button backButton = (Button)this.findViewById(R.id.backbtn);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker.speak("Back");
                finish();
            }
        });
        Button ADD = (Button)findViewById(R.id.btn_add);
        ADD.setOnClickListener(addcontact);

    }


}

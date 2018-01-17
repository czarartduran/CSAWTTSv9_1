package com.patchie.csawttsv9;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Czar Art Z. Duran on 14/01/2018.
 */

public class CSB{
    private Context LocalContext;
    private int smsMAX = 10;

    public CSB(){
        //codes here
    }

    public CSB(Context context){
        LocalContext = context;
        Load_smslist(context);
    }

    public CSB(Context context, int maxsms){
        LocalContext = context;
        smsMAX = maxsms;
        Load_smslist(context);
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

    private ArrayList<String> _smslist;

    private void Load_smslist(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        Log.e("Czar", "CursorSize: " + smsInboxCursor.getCount());

        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexDateSent = smsInboxCursor.getColumnIndex("date");

        //Uncomment this to see all column included in the sms query
        /*String[] asx = smsInboxCursor.getColumnNames();
        for (int i=0; i < asx.length; i++){
            Log.e("Czar","SMS query Index " + i + ": " + asx[i]);
        }*/

        if (smsInboxCursor.getCount() > 0 && smsInboxCursor.moveToFirst()) {
            _smslist = new ArrayList<>();

            String addressIntro = context.getString(R.string.AddressIntro) + " ";
            String bodyIntro = context.getString(R.string.BodyIntro) + " ";
            Date sentDate = new Date();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            int smsCTR = 0;
            do {
                //Uncomment this to see full data output in the query
                /*String[] asx = smsInboxCursor.getColumnNames();
                String res = "";
                for (int i=0; i < asx.length; i++){
                    res += "Index " + i + " " + asx[i] + " : " + smsInboxCursor.getString(i) + " || ";
                }
                Log.e("Czar",res);*/

                sentDate = new Date(Long.parseLong(smsInboxCursor.getString(indexDateSent)));

                String str = "\n" + addressIntro + getContactName(context,
                        smsInboxCursor.getString(indexAddress)) + "\n" +
                        bodyIntro + smsInboxCursor.getString(indexBody) + "\n" +
                        "Date Sent: " + df.format(sentDate) + "\n";

                if (smsCTR < smsMAX){
                    smsCTR++;
                    _smslist.add(str);
                }else {
                    break;
                }
            } while (smsInboxCursor.moveToNext());
        }
    }

    public ArrayList<String> SMSLIST() {
        ArrayList<String> ans = new ArrayList<>();
        if (_smslist == null || _smslist.size() == 0 && LocalContext != null){
            Load_smslist(LocalContext);
            Log.e("Czar", "Loading _smslist");
            return _smslist;
        }else {
            return _smslist;
        }
    }

    private ArrayList<String> _contactList = new ArrayList<>();
    private ArrayList<String[]> filtercontactlist = new ArrayList<>();
    private ArrayList<String> Load_contactlist(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        /*Testing*/
        /*String[] strTest = contactCursor.getColumnNames();
        String res = "";
        for (int i = 0; i < strTest.length; i++){
            res += i + ": " + strTest[i] + " |" + "\n";
        }
        Log.e("Czar", "Res= \n" + res);
        contactCursor.moveToFirst();
        do {
            res ="";
            for (int i = 0; i < strTest.length; i++){
                res += i +" : " + strTest[i] + ": " + contactCursor.getString(i) + "\n";
            }
            Log.e("Czar", "\n" + res);
        }while (contactCursor.moveToNext());*/

        int nameINDEX = contactCursor.getColumnIndex("display_name"); //6
        int numberINDEX = contactCursor.getColumnIndex("data1"); //data4, data1
        if (contactCursor.getCount() > 0 && contactCursor.moveToFirst()){
            _contactList = new ArrayList<>();
            String ContactName = "";
            String Contactnumber = "";
            String filtercon[] = new String[2]; //0:name | 1:number
            do {
                filtercon= new String[2]; //0:name | 1:number
                String str = "";
                ContactName = contactCursor.getString(nameINDEX);
                filtercon[0] = contactCursor.getString(nameINDEX);
                Contactnumber = "Contact Number: " + contactCursor.getString(numberINDEX);
                filtercon[1] = contactCursor.getString(numberINDEX);
                str += ContactName + "\n" + Contactnumber;
                _contactList.add(str);
                filtercontactlist.add(filtercon);

            }while (contactCursor.moveToNext());
        }
        return _contactList;
    }

    public ArrayList<String> CONTACTLIST(){
        return Load_contactlist(LocalContext);
    }

    public String RecipientName(int selectedINDEX){
        Load_contactlist(LocalContext);
        String str[] =  (String[]) filtercontactlist.get(selectedINDEX);
        return str[0];
    }

    public String RecipientNumber(int selectedINDEX){
        Load_contactlist(LocalContext);
        String str[] =  (String[]) filtercontactlist.get(selectedINDEX);
        return str[1];
    }
}

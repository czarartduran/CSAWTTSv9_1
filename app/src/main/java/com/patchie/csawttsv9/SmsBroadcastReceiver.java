package com.patchie.csawttsv9;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    Speaker speaker;

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            String address = "";
            String smsBody = "";
            long smsDate = 0;

            String addressIntro = context.getString(R.string.AddressIntro) + " ";
            String bodyIntro = context.getString(R.string.BodyIntro) + " ";

            String smsMessageStr = "";

            Log.e("Czar","sms.length:" + sms.length);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();
                smsDate = smsMessage.getTimestampMillis();

                Date SmsDate = new Date(smsDate);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                /*smsMessageStr += addressIntro + address + "\n";
                smsMessageStr += bodyIntro + smsBody + "\n";*/

                smsMessageStr += "\n";
                smsMessageStr += addressIntro + address + "\n";
                smsMessageStr += bodyIntro + smsBody + "\n";
                smsMessageStr += "Date Sent: " + df.format(SmsDate);
                smsMessageStr += "\n";
            }


            Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();

            if (SMSActivity.active) {
                SMSActivity inst = SMSActivity.instance();
                inst.updateInbox(smsMessageStr);
            } else if (!MainActivity.active){
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        }
    }
}
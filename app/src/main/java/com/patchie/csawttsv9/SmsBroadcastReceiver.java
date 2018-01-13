package com.patchie.csawttsv9;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            String address = "";
            String smsBody = "";

            String addressIntro = context.getString(R.string.AddressIntro) + " ";
            String bodyIntro = context.getString(R.string.BodyIntro) + " ";

            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();

                /*smsMessageStr += addressIntro + address + "\n";
                smsMessageStr += bodyIntro + smsBody + "\n";*/

                smsMessageStr += addressIntro + address + "\n";
                smsMessageStr += bodyIntro + smsBody;
            }


            Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();

            if (SMSActivity.active) {
                SMSActivity inst = SMSActivity.instance();
                inst.updateInbox(smsMessageStr);
            } else {
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }

        }
    }
}
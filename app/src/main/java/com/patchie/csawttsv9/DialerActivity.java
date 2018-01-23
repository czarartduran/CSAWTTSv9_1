package com.patchie.csawttsv9;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class DialerActivity extends AppCompatActivity {
    private String PhoneNumber = "";
    EditText editNum;
    Speaker speaker;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        speaker = new Speaker(getApplicationContext(), "Welcome to Dial Module, you can now input your desire number to call. Press C " +
                "to call inputted number, press X to delete last inputted number and press B to back to previous module");
t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            t1.setLanguage(Locale.ENGLISH);
        }

    }
});
        //assigning number_tb
        editNum = (EditText)findViewById(R.id.number_tb);


    }

    public void numPad_OnClickEvent(View view) {
        NumPad(view);
    }

    protected void NumPad(View v){
        switch (v.getId()){
            case R.id.btn1:
                AppendNumber("1");t1.speak("1",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn2:
                AppendNumber("2");t1.speak("2",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn3:
                AppendNumber("3");t1.speak("3",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn4:
                AppendNumber("4");t1.speak("4",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn5:
                AppendNumber("5");t1.speak("5",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn6:
                AppendNumber("6");t1.speak("6",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn7:
                AppendNumber("7");t1.speak("7",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn8:
                AppendNumber("8");t1.speak("8",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn9:
                AppendNumber("9");t1.speak("9",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn10:
                AppendNumber("0");t1.speak("0",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn11:
                AppendNumber("*");t1.speak("*",TextToSpeech.QUEUE_FLUSH, null);
            break;
            case R.id.btn12:
                AppendNumber("#");t1.speak("#",TextToSpeech.QUEUE_FLUSH, null);
            break;
            default:
                break;
        }
    }

    private void AppendNumber(String num){
        Log.e("Czar", num);
        //PhoneNumber = PhoneNumber + num;
        editNum.append(num);
        PhoneNumber = editNum.getText().toString();
    }

    public void backspace_OnClickEvent(View view) {
        t1.speak("Delete",TextToSpeech.QUEUE_FLUSH, null);
        BackSpace();
    }

    private void BackSpace(){
        String old = editNum.getText().toString();
        String newStr ="";
        //tb
        editNum.setText("");
        if (old.length() > 0){
            newStr = old.substring(0, old.length() -1);
            PhoneNumber = newStr;
            editNum.append(newStr);
        }else {
            editNum.setText("");
        }
    }

    public void cancelbtn_OnClickEvent(View view) {
        CancelBtn();
    }

    private void CancelBtn(){
        finish();
    }

    public void dialbtn_OnClickEvent(View view) {
        Call();
    }

    private void Call(){
        t1.speak("Call",TextToSpeech.QUEUE_FLUSH, null);
        Intent callIntent;
        callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PhoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
        String toSpeak = editNum.getText().toString();
        t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);

    }
    public void repeatbtn_OnClickEvent(View v){
        repeat();
    }
    private void repeat(){

        String toSpeak = PhoneNumber;
        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }


}

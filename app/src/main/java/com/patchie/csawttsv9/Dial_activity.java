//5:47pm 1/12/18 jibeh
package com.patchie.csawttsv9;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import java.util.Locale;


public class Dial_activity extends AppCompatActivity implements View.OnClickListener {
    private EditText ET1;
    TextToSpeech t1;
    Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_activity);
        //example
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
        initializeView();
    }

    private void initializeView() {
        ET1 = (EditText)findViewById(R.id.ET1);
        int Listid[] = { R.id.btn0,R.id.btn1,R.id.btn2,
                R.id.btn3,R.id.btn4,R.id.btn5,
                R.id.btn6,R.id.btn7,R.id.btn8,
                R.id.btn9,R.id.btnast,R.id.btnhash,
                R.id.btncall,R.id.btndel,R.id.backbtn};
        for(int d: Listid){
            View v = (View)findViewById(d);
            v.setOnClickListener(this);

        }
    }

    public void display(String val){
        ET1.append(val);
    }

    private boolean checkCallPermission(){
        String permission = "android.permission.CALL_PHONE";
        int res = getApplicationContext() .checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                display("1");
                t1.speak("1",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn2:
                display("2");
                t1.speak("2",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn3:
                display("3");
                t1.speak("3",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn4:
                display("4");
                t1.speak("4",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn5:
                display("5");
                t1.speak("5",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn6:
                display("6");
                t1.speak("6",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn7:
                display("7");
                t1.speak("7",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn8:
                display("8");
                t1.speak("8",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn9:
                display("9");
                t1.speak("9",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btn0:
                display("0");
                t1.speak("0",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btnast:
                display("*");
                t1.speak("*",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btnhash:
                display("#");
                t1.speak("#",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.btncall:
                t1.speak("Call",TextToSpeech.QUEUE_FLUSH, null);
                if(ET1.getText().toString() .isEmpty())
                    t1.speak("Please Enter some digits", TextToSpeech.QUEUE_FLUSH, null);
                else if(checkCallPermission())
                    t1.speak("Now calling...", TextToSpeech.QUEUE_FLUSH, null);
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ET1.getText())));
                break;
            case R.id.btndel:
                t1.speak("Delete",TextToSpeech.QUEUE_FLUSH, null);
                if(ET1.getText().toString().length()>=1){
                    String newScreen = ET1.getText().toString().substring(0, ET1.getText().toString().length() - 1);
                    ET1.setText(newScreen);
                }
                break;
            case R.id.backbtn:
                t1.speak("Back",TextToSpeech.QUEUE_FLUSH, null);
                finish();
                break;

            default:
                break;
        }
        String toSpeak = ET1.getText().toString();
        t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
    }
}

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

public class DialerActivity extends AppCompatActivity {
    private String PhoneNumber = "";
    EditText editNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        //assigning number_tb
        editNum = (EditText)findViewById(R.id.number_tb);


    }

    public void numPad_OnClickEvent(View view) {
        NumPad(view);
    }

    protected void NumPad(View v){
        switch (v.getId()){
            case R.id.btn1: AppendNumber("1"); break;
            case R.id.btn2: AppendNumber("2"); break;
            case R.id.btn3: AppendNumber("3"); break;
            case R.id.btn4: AppendNumber("4"); break;
            case R.id.btn5: AppendNumber("5"); break;
            case R.id.btn6: AppendNumber("6"); break;
            case R.id.btn7: AppendNumber("7"); break;
            case R.id.btn8: AppendNumber("8"); break;
            case R.id.btn9: AppendNumber("9"); break;
            case R.id.btn10: AppendNumber("0"); break;
            case R.id.btn11: AppendNumber("*"); break;
            case R.id.btn12: AppendNumber("#"); break;
            default: break;
        }
    }

    private void AppendNumber(String num){
        Log.e("Czar", num);
        //PhoneNumber = PhoneNumber + num;
        editNum.append(num);
        PhoneNumber = editNum.getText().toString();
    }

    public void backspace_OnClickEvent(View view) {
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
    }
}

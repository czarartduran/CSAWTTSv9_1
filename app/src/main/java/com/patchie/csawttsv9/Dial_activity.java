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


public class Dial_activity extends AppCompatActivity implements View.OnClickListener {
    private EditText ET1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_activity);
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
                break;
            case R.id.btn2:
                display("2");
                break;
            case R.id.btn3:
                display("3");
                break;
            case R.id.btn4:
                display("4");
                break;
            case R.id.btn5:
                display("5");
                break;
            case R.id.btn6:
                display("6");
                break;
            case R.id.btn7:
                display("7");
                break;
            case R.id.btn8:
                display("8");
                break;
            case R.id.btn9:
                display("9");
                break;
            case R.id.btn0:
                display("0");
                break;
            case R.id.btnast:
                display("*");
                break;
            case R.id.btnhash:
                display("#");
                break;
            case R.id.btncall:
                if(ET1.getText().toString() .isEmpty())
                    Toast.makeText(getApplicationContext(), "Please Enter digits",Toast.LENGTH_SHORT).show();
                else if(checkCallPermission())
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ET1.getText())));
                break;
            case R.id.btndel:
                if(ET1.getText().toString().length()>=1){
                    String newScreen = ET1.getText().toString().substring(0, ET1.getText().toString().length() - 1);
                    ET1.setText(newScreen);
                }
                break;
            case R.id.backbtn:
                finish();
                break;

            default:
                break;
        }
    }
}

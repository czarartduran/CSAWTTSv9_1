package com.patchie.csawttsv9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CallActivityV2 extends AppCompatActivity {
    CSB csb;
    Speaker _speak;
    private int selectedIndex = 0;
    private String SELECTED_NAME, SELECTED_NUMBER;

    ListView contact_lv;
    ArrayList<String> contactlist;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_v2);
        setTitle(getString(R.string.CallerActivity));

        _speak = new Speaker(getApplicationContext());

        contact_lv = findViewById(R.id.call_contact_lv);
        if (contactlist == null){
            Log.e("Czar", "Initialized contact list");
            csb = new CSB(this);
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, csb.CONTACTLIST());
        }
        contact_lv.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _speak = new Speaker(getApplicationContext());
        Speak("Please select contact");
    }

    private void Speak(String string){
        _speak.speak(string);
    }

    public void sra_prev_btn_OnClickEvent(View view) {
        sra_prev_btn();
    }

    private void sra_prev_btn(){
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= 0 && selectedIndex < lv_count){
            if (selectedIndex >=0 && selectedIndex -1 >= 0){
                selectedIndex--;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }else {
                selectedIndex = 0;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_sel_btn_OnClickEvent(View view) {
        sra_sel_btn();
    }

    private void sra_sel_btn(){
        //Log.e("Czar", "RecipientName: " + csb.RecipientName(selectedIndex));
        /*SELECTED_NAME = csb.RecipientName(selectedIndex);
        SELECTED_NUMBER = csb.RecipientNumber(selectedIndex);
        Intent intent = new Intent();
        intent.putExtra("CONTACT_NAME", SELECTED_NAME);
        intent.putExtra("CONTACT_NUMBER", SELECTED_NUMBER);
        setResult(RESULT_OK, intent);*/
        finish();
    }

    public void sra_next_btn_OnClickEvent(View view) {
        sra_next_btn();
    }

    private void sra_next_btn(){
        int lv_count = this.contact_lv.getCount();
        if (selectedIndex >= -1 && selectedIndex < lv_count){
            if (selectedIndex < lv_count && selectedIndex +1 < lv_count){
                selectedIndex++;
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }else {
                Speak(this.contact_lv.getItemAtPosition(selectedIndex).toString());
            }
        }
    }

    public void sra_can_btn_OnClickEvent(View view) {
        sra_can_btn();
    }

    private void sra_can_btn(){
        finish();
    }
}

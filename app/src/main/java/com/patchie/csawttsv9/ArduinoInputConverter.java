package com.patchie.csawttsv9;

import android.util.Log;

/**
 * Created by Czar Art Z. Duran on 23/01/2018.
 */

public class ArduinoInputConverter {
    /*
    * standard 0-9a-zA-Z
    * */
    //a-z
    private static int MIN_NCAPS = 97;
    private static int MAX_NCAPS = 122;
    //A-Z
    private static int MIN_CAPS = 65;
    private static int MAX_CAPS = 90;
    //0-9
    private static int MIN_NUMBERS = 48;
    private static int MAX_NUMBERS = 57;

    public String getChar(String input) {
        String ans = "";
        int x = Integer.parseInt(input);
        Log.e("ArduinoInputConverter", "Input Conversion: " + x);

        char c = (char)x;

        if (x >= MIN_CAPS && x <= MAX_CAPS) {
            ans = String.valueOf(c);
        } else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
            ans = String.valueOf(c);
        }else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS){
            ans = String.valueOf(c);
        }else {
            //must have another function before returning null;
            ans = null;
        }

        return ans;
    }

    public boolean isAcceptableInput(String input){
        boolean ans = false;
        int x = Integer.parseInt(input);

        if (x >= MIN_CAPS && x <= MAX_CAPS) {
            ans = true;
        } else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
            ans = true;
        }else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS){
            ans = true;
        }else {
            //must have another function before returning null;
            ans = false;
        }

        return ans;
    }
}

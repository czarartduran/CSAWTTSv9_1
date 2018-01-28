package com.patchie.csawttsv9;

import android.content.Context;
import android.util.Log;

/**
 * Created by Czar Art Z. Duran on 23/01/2018.
 */

public class ArduinoInputConverter {


    private Context context;

    public ArduinoInputConverter() {

    }

    public ArduinoInputConverter(Context context) {
        this.context = context;
        FillVariable();
    }

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

    private static int[] Customs = {46, 32, 40, 57,  63, 41, 39};

    public boolean IsNotEmpty(String input) {
        boolean ans = false;
        if (input.length() > 0) {
            ans = true;
        }
        return ans;
    }

    public String getChar(String input) {
        String ans = "";
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            Log.e("ArduinoInputConverter", "Input Conversion: " + x);

            char c = (char) x;

            if (x >= MIN_CAPS && x <= MAX_CAPS) {
                ans = String.valueOf(c);
            } else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
                ans = String.valueOf(c);
            } else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS) {
                ans = String.valueOf(c);
            } else if (InCustom(input)) {
                //must have another function before returning null;
                ans = String.valueOf(c);
            }
        }
        return ans;
    }

    public boolean IsSame(String input, int ref) {
        boolean ans = false;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            if (x == ref) {
                ans = true;
            }
        }
        return ans;
    }

    public boolean IsForMessaging(String input) {
        boolean ans = false;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            if (x >= MIN_CAPS && x <= MAX_CAPS) {
                ans = true;
            } /*else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
                ans = true;
            }*/ else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS) {
                ans = true;
            } else if (InCustom(input)) {
                //must have another function before returning null;
                ans = true;
            }
        }
        return ans;
    }

    public boolean InCustom(String input) {
        boolean ans = false;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            for (int i = 0; i < Customs.length; i++) {
                if (x == Customs[i]) {
                    ans = true;
                }
            }
        }
        return ans;
    }

    public boolean isAcceptableInput(String input) {
        boolean ans = false;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            if (x >= MIN_CAPS && x <= MAX_CAPS) {
                ans = true;
            } else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
                ans = true;
            } else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS) {
                ans = true;
            } else {
                //must have another function before returning null;
                ans = false;
            }
        }
        return ans;
    }

    public boolean IsNumber(String input) {
        boolean ans = false;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);
            if (x >= MIN_CAPS && x <= MIN_CAPS + 9) {
                ans = true;
            }
        }
        return ans;
    }

    public int GetNumber(String input) {
        int ans = -1;
        if (IsNotEmpty(input)) {
            int x = Integer.parseInt(input);

            if (IsNumber(input)) {
                switch (x) {
                    case 65:
                        ans = 1;
                        break;
                    case 66:
                        ans = 2;
                        break;
                    case 67:
                        ans = 3;
                        break;
                    case 68:
                        ans = 4;
                        break;
                    case 69:
                        ans = 5;
                        break;
                    case 70:
                        ans = 6;
                        break;
                    case 71:
                        ans = 7;
                        break;
                    case 72:
                        ans = 8;
                        break;
                    case 73:
                        ans = 9;
                        break;
                    case 74:
                        ans = 0;
                        break;
                }
            }
        }
        return ans;
    }

    /*Returning the decimal value of a given input*/
    public int getDecimal(String input) {
        if (IsNotEmpty(input)) {
            return Integer.parseInt(input);
        } else {
            return -1;
        }
    }

    /*
    * <string name="CONTROL_CANCEL"></string>
    <string name="CONTROL_OK"></string>
    <string name="CONTROL_PREVIOUS"></string>
    <string name="CONTROL_NEXT"></string>
    <string name="CONTROL_COMPOSE"></string>
    <string name="CONTROL_REPLY"></string>
    <string name="CONTROL_SEARCH"></string>
    <string name="CONTROL_SPACE"></string>
    <string name="CONTROL_BACKSPACE">64</string>
    <string name="CONTROL_FOCUS_CHANGER"></string>
    */

    private int _CONTROL_CANCEL;
    private int _CONTROL_OK;
    private int _CONTROL_PREVIOUS;
    private int _CONTROL_NEXT;
    private int _CONTROL_COMPOSE;
    private int _CONTROL_REPLY;
    private int _CONTROL_SEARCH;
    private int _CONTROL_SPACE;
    private int _CONTROL_BACKSPACE;
    private int _CONTROL_FOCUS_CHANGER;
    private int _CONTROL_CALL_ACTIVITY;
    private int _CONTROL_SMS_ACTIVITY;

    /*private int _CONTROL_ADD_CONTACT;
    private int _CONTROL_DIALER;
    private int _CONTROL_PREV_CALL;
    private int _CONTROL_NEXT_CALL;
    private int _CONTROL_ADD_ADDCONTACT;
    private int _CONTROL_CALL_DIALER;
    private int _CONTROL_CANCELV2;
    private int _CONTROL_SELECT_CALL;*/

    private void FillVariable() {
        _CONTROL_CANCEL = Integer.parseInt(context.getString(R.string.CONTROL_CANCEL));
        _CONTROL_OK = Integer.parseInt(context.getString(R.string.CONTROL_OK));
        _CONTROL_PREVIOUS = Integer.parseInt(context.getString(R.string.CONTROL_PREVIOUS));
        _CONTROL_NEXT = Integer.parseInt(context.getString(R.string.CONTROL_NEXT));
        _CONTROL_COMPOSE = Integer.parseInt(context.getString(R.string.CONTROL_COMPOSE));
        _CONTROL_REPLY = Integer.parseInt(context.getString(R.string.CONTROL_REPLY));
        _CONTROL_SEARCH = Integer.parseInt(context.getString(R.string.CONTROL_SEARCH));
        _CONTROL_SPACE = Integer.parseInt(context.getString(R.string.CONTROL_SPACE));
        _CONTROL_BACKSPACE = Integer.parseInt(context.getString(R.string.CONTROL_BACKSPACE));
        _CONTROL_FOCUS_CHANGER = Integer.parseInt(context.getString(R.string.CONTROL_FOCUS_CHANGER));
        _CONTROL_CALL_ACTIVITY = Integer.parseInt(context.getString(R.string.CONTROL_CALL_ACTIVITY));
        _CONTROL_SMS_ACTIVITY = Integer.parseInt(context.getString(R.string.CONTROL_SMS_ACTIVITY));

        /*_CONTROL_ADD_CONTACT = Integer.parseInt(context.getString(R.string.CONTROL_ADD_CONTACT));
        _CONTROL_DIALER = Integer.parseInt(context.getString(R.string.CONTROL_DIALER));
        _CONTROL_PREV_CALL = Integer.parseInt(context.getString(R.string.CONTROL_PREV_CALL));
        _CONTROL_NEXT_CALL = Integer.parseInt(context.getString(R.string.CONTROL_NEXT_CALL));
        _CONTROL_CALL_DIALER = Integer.parseInt(context.getString(R.string.CONTROL_CALL_DIALER));
        _CONTROL_ADD_ADDCONTACT = Integer.parseInt(context.getString(R.string.CONTROL_ADD_ADDCONTACT));
        _CONTROL_CANCELV2 = Integer.parseInt(context.getString(R.string.CONTROL_CANCELV2));
        _CONTROL_SELECT_CALL = Integer.parseInt(context.getString(R.string.CONTROL_SELECT_CALL));*/

    }

    public int CONTROL_CANCEL() {
        return _CONTROL_CANCEL;
    }

    public int CONTROL_OK() {
        return _CONTROL_OK;
    }

    public int CONTROL_PREVIOUS() {
        return _CONTROL_PREVIOUS;
    }

    public int CONTROL_NEXT() {
        return _CONTROL_NEXT;
    }

    public int CONTROL_COMPOSE() {
        return _CONTROL_COMPOSE;
    }

    public int CONTROL_REPLY() {
        return _CONTROL_REPLY;
    }

    public int CONTROL_SEARCH() {
        return _CONTROL_SEARCH;
    }

    public int CONTROL_SPACE() {
        return _CONTROL_SPACE;
    }

    public int CONTROL_BACKSPACE() {
        return _CONTROL_BACKSPACE;
    }

    public int CONTROL_FOCUS_CHANGER(){
        return _CONTROL_FOCUS_CHANGER;
    }

    public int CONTROL_CALL_ACTIVITY() {
        return _CONTROL_CALL_ACTIVITY;
    }

    public int CONTROL_SMS_ACTIVITY() {
        return _CONTROL_SMS_ACTIVITY;
    }

    /*public int CONTROL_ADD_CONTACT(){
        return _CONTROL_ADD_CONTACT;
    }
    public int CONTROL_DIALER(){
        return _CONTROL_DIALER;

    }
    public int CONTROL_PREV_CALL(){
        return _CONTROL_PREV_CALL;

    }
    public int CONTROL_NEXT_CALL(){
        return _CONTROL_NEXT_CALL;

    }
    public int CONTROL_CALL_DIALER(){
        return _CONTROL_CALL_DIALER;

    }
    public int CONTROL_ADD_ADDCONTACT(){
        return _CONTROL_ADD_ADDCONTACT;

    }
    public int CONTROL_CANCELV2(){
        return _CONTROL_CANCELV2;

    }
    public int CONTROL_SELECT_CALL(){
        return _CONTROL_SELECT_CALL;
    }*/

}

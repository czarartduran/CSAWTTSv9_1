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
    private static int[] Customs = {46, 32, 40, 57, 32, 63};

    public String getChar(String input) {
        if (input.length() == 0) {
            return "";
        }
        String ans = "";
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

        return ans;
    }

    public boolean IsSame(String input, int ref) {
        if (input.length() == 0) {
            return false;
        }
        boolean ans = false;
        int x = Integer.parseInt(input);
        if (x == ref) {
            ans = true;
        }
        return ans;
    }

    public boolean IsForMessaging(String input) {
        if (input.length() == 0) {
            return false;
        }
        boolean ans = false;
        int x = Integer.parseInt(input);
        if (x >= MIN_CAPS && x <= MAX_CAPS) {
            ans = true;
        } else if (x >= MIN_NCAPS && x <= MAX_NCAPS) {
            ans = true;
        } else if (x >= MIN_NUMBERS && x <= MAX_NUMBERS) {
            ans = true;
        } else if (InCustom(input)) {
            //must have another function before returning null;
            ans = true;
        }
        return ans;
    }

    public boolean InCustom(String input) {
        boolean ans = false;
        int x = Integer.parseInt(input);
        for (int i = 0; i < Customs.length; i++) {
            if (x == Customs[i]) {
                ans = true;
            }
        }
        return ans;
    }

    public boolean isAcceptableInput(String input) {
        boolean ans = false;
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

        return ans;
    }

    public boolean IsNumber(String input) {
        boolean ans = false;
        int x = Integer.parseInt(input);
        if (x >= MIN_CAPS && x <= MIN_CAPS + 9) {
            ans = true;
        }
        return ans;
    }

    public int GetNumber(String input) {
        int ans = -1;
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
        return ans;
    }

    /*Returning the decimal value of a given input*/
    public int getDecimal(String input) {
        return Integer.parseInt(input);
    }
}

package com.hakai.utils;

public class MathUtils {

    public static boolean isInteger(String s)
    {
        try {
            Integer.parseInt(s);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }
}

package com.heady.util;

/**
 * Created by Yogi.
 */

public class StringUtil {

    /**
     * Check whether a string is not NULL, empty or "NULL", "null", "Null"
     */
    public static boolean isNotEmpty(String str) {
        boolean flag = true;
        if (str != null) {
            str = str.trim();
            if (str.length() == 0 || str.equalsIgnoreCase("null")) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}

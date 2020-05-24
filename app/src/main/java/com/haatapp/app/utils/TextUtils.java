package com.haatapp.app.utils;

/**
 * Created by Tamil on 9/21/2017.
 */

public class TextUtils {

    //Check empty edit text
    public static boolean isEmpty(String strText) {
        return strText.length() == 0;
    }

    //check Valid Mail address
    public final static boolean isValidEmail(String strText) {
        return strText != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strText).matches();
    }

}
